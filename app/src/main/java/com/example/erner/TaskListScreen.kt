package com.example.erner

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.erner.ui.theme.DeepRed
import com.example.erner.ui.theme.DeepYellow
import com.example.erner.ui.theme.LinkColor
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    title: String,
    titleColor: Color,
    tasks: List<TaskEntity>,
    onBack: () -> Unit = {}
) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var deleting by remember { mutableStateOf(false) }

    var showClearDialog by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "tasks") {
        composable("tasks") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title, color = titleColor) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            // "Clear completed" only visible if viewing completed tasks
                            if (title == "Completed") {
                                TextButton(onClick = { showClearDialog = true }) {
                                    Text("Clear", color = LinkColor)
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggleCompletion = { taskId ->
                                    viewModel.toggleTaskCompletion(
                                        taskId
                                    )
                                },
                                onClick = {
                                    viewModel.selectTask(task)
                                    navController.navigate("edit_task")
                                },
                                onLongPress = { taskToDelete = task}
                            )
                        }
                    }
                    // Confirm delete dialog
                    if (taskToDelete != null) {
                        AlertDialog(
                            onDismissRequest = { if (!deleting) taskToDelete = null },
                            title = { Text("Delete task") },
                            text = { Text("Tasks deleted cannot be recovered.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (deleting) return@TextButton
                                        deleting = true
                                        val task = taskToDelete!!
                                        scope.launch {
                                            kotlinx.coroutines.delay(2000)
                                            viewModel.deleteTask(task)
                                            deleting = false
                                            taskToDelete = null
                                        }
                                    }
                                ) { Text(if (deleting) "Deleting…" else "Delete") }
                            },
                            dismissButton = {
                                TextButton(
                                    enabled = !deleting,
                                    onClick = { taskToDelete = null }
                                ) { Text("Cancel") }
                            }
                        )

                    }
                    // Confirm clear-all dialog
                    if (showClearDialog) {
                        AlertDialog(
                            onDismissRequest = { if (!deleting) showClearDialog = false },
                            title = { Text("Discard all completed tasks") },
                            text = { Text("Tasks deleted cannot be recovered.") },
                            confirmButton = {
                                TextButton(
                                    enabled = !deleting,
                                    onClick = {
                                        deleting = true
                                        scope.launch {
                                            kotlinx.coroutines.delay(2000)   // ⏱ 2.5s delay
                                            viewModel.clearCompleted()       // bulk delete
                                            deleting = false
                                            showClearDialog = false
                                        }
                                    }
                                ) { Text(if (deleting) "Deleting…" else "Delete") }
                            },
                            dismissButton = {
                                TextButton(
                                    enabled = !deleting,
                                    onClick = { showClearDialog = false }
                                ) { Text("Cancel") }
                            }
                        )
                    }

                }
            }
        }

        composable("edit_task") { backStackEntry ->
            val task = viewModel.selectedTask
            if (task != null) {
                EditTaskScreen(
                    onCancel = { navController.popBackStack() },
                    initialTask = task,
                    onSave = { title, notes, deadline, estimatedDuration, reminderTime,
                               isRepeatEnabled, repeatType, isFlagged, isProofRequired, proofType ->
                        val updatedTask = task.copy (
                            taskTitle = title.trim(),
                            taskNotes = notes.trim(),
                            taskDeadline = deadline,
                            taskEstimatedDuration = estimatedDuration,
                            taskReminderTime = reminderTime,
                            taskIsRepeatEnabled = isRepeatEnabled,
                            taskRepeatType = repeatType,
                            taskIsFlagged = isFlagged,
                            taskIsProofRequired = isProofRequired,
                            taskProofType = proofType,
                            taskProjectName = task.taskProjectName,
                            taskIsCompleted = task.taskIsCompleted,
                            )
                        viewModel.updateTask(updatedTask)
                        navController.popBackStack()
                    }
                )
            }
        }

    }

}

@Composable
fun TaskItem(
    task: TaskEntity,
    onToggleCompletion: (Long) -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    var isChecked by remember { mutableStateOf(task.taskIsCompleted)}
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(end = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(2500)
                        if (isChecked != task.taskIsCompleted)
                            onToggleCompletion(task.id)
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.taskTitle)
                if (task.taskNotes.isNotEmpty()) {
                    Text(task.taskNotes, fontSize = 14.sp, color = Color.Gray)
                }
                Row {
                    if (!task.taskProjectName.isNullOrEmpty()) {
                        Text(task.taskProjectName, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    val isPast = task.taskReminderTime?.isBefore(LocalDateTime.now()) == true
                    Text(
                        task.taskReminderTime?.format(DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a"))
                            ?: "",
                        fontSize = 12.sp, color = if (isPast && !task.taskIsCompleted) DeepRed else Color.Gray)
                }
            }
            if (task.taskIsFlagged) {
                Icon(Icons.Default.Flag, contentDescription = "Menu", tint = DeepYellow)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}