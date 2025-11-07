package com.example.erner

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.erner.ui.theme.ErnerBottomSheetTheme
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddTaskBottomSheet : BottomSheetDialogFragment() {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {


        setContent {
            ErnerBottomSheetTheme {
                AddTaskScreen(
                    onCancel = { dismiss() },
                    onAdd = { title, notes, deadline, estimatedDuration, reminderTime,
                              isRepeatEnabled, repeatType, isFlagged, isProofRequired, proofType ->
                        if (isProofRequired) {
                            if (!hasInternetConnection(requireContext())) {
                                Toast.makeText(
                                    requireContext(),
                                    "You need an internet connection to create proof‑required tasks",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {

                                // Format deadline & duration
                                val deadlineStr = deadline?.format(
                                    DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a", Locale.getDefault())
                                ) ?: "Not specified"
                                val durationStr = estimatedDuration?.let { (h, m) -> "${h}h ${m}m" } ?: "Not specified"

                                // Build prompt (tiny grammar tweak on that “relevant to”)
                                val prompt = """
                                    You are an assistant that generates proof requirements for tasks in a productivity app.
                                    Your job is to output ONE clear sentence describing the exact type of file the user should upload as proof of completing the task.
                                    Be specific, practical, and mention the required file type ("image" or "text document" only).
                                    Always factor important task details such as the title, notes, deadline, and duration when relevant. For eg. if deadline is important, mention the exact date and time before when completion is permitted.
                                    Write in a professional but concise style. Limit your response to 1–2 sentences.
                                    If it's not possible and/or practical to provide proof for a certain task, just respond with "No proof of completion required for this task" only.
                            
                                    Task details:
                                    Title: $title
                                    Notes: $notes
                                    Deadline: $deadlineStr
                                    Duration: $durationStr
                            
                                    Output format (if proof of completion is required):
                                    File type: <type>. Description: <clear and specific proof requirement>.
                                """.trimIndent()

                                // Tie to VIEW lifecycle; catch cancellation; dismiss after insert completes
                                viewLifecycleOwner.lifecycleScope.launch {
                                    try {
                                        // Optional: withTimeout(15_000) { ... }
                                        val response = withContext(Dispatchers.IO) {
                                            model.generateContent(prompt)
                                        }

                                        val proofText = response.text?.trim().orEmpty().ifEmpty {
                                            // Fallback if API returned empty text
                                            "File type: Image. Description: Photo or scan clearly showing evidence relevant to the task and date."
                                        }

                                        val newTask = TaskEntity(
                                            taskTitle = title.trim(),
                                            taskNotes = notes.trim(),
                                            taskDeadline = deadline,
                                            taskEstimatedDuration = estimatedDuration,
                                            taskReminderTime = reminderTime,
                                            taskIsRepeatEnabled = isRepeatEnabled,
                                            taskRepeatType = repeatType,
                                            taskIsFlagged = isFlagged,
                                            taskIsProofRequired = true,
                                            taskProofType = proofText,
                                            taskProjectName = null,
                                            taskIsCompleted = false,
                                        )

                                        withContext(Dispatchers.IO) {
                                            AppDatabase.getDatabase(requireContext()).taskDao()
                                                .insertTask(newTask)
                                        }

                                        if (isAdded) dismiss() // close only after success

                                    } catch (e: com.google.firebase.ai.type.FirebaseAIException) {
                                        if (isAdded) {
                                            Toast.makeText(
                                                requireContext(),
                                                "AI generation failed: ${e.message ?: "Please try again."}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        if (isAdded) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Something went wrong: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        } else {
                            val newTask = TaskEntity(
                                taskTitle = title.trim(),
                                taskNotes = notes.trim(),
                                taskDeadline = deadline,
                                taskEstimatedDuration = estimatedDuration,
                                taskReminderTime = reminderTime,
                                taskIsRepeatEnabled = isRepeatEnabled,
                                taskRepeatType = repeatType,
                                taskIsFlagged = isFlagged,
                                taskIsProofRequired = false,
                                taskProofType = null,
                                taskProjectName = null,
                                taskIsCompleted = false,
                            )

                            viewLifecycleOwner.lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    AppDatabase.getDatabase(requireContext()).taskDao().insertTask(newTask)
                                }
                                if (isAdded) dismiss()
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT // Full height
        )
    }
}