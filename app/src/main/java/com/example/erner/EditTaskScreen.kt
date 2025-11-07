package com.example.erner

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.erner.ui.theme.DarkGrey
import com.example.erner.ui.theme.DeepBlue
import com.example.erner.ui.theme.DeepGreen
import com.example.erner.ui.theme.DeepYellow
import com.example.erner.ui.theme.LinkColor
import com.example.erner.ui.theme.OnColor
import com.example.erner.ui.theme.PureWhite
import com.example.erner.ui.theme.VeryLightGrey
import androidx.compose.material3.AlertDialog
import com.example.erner.ui.theme.Grey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    onCancel: () -> Unit = {},
    initialTask: TaskEntity,
    onSave: (
        title: String,
        notes: String,
        deadline: LocalDateTime?,
        estimatedDuration: Pair<Int, Int>?,
        reminderTime: LocalDateTime?,
        repeatEnabled: Boolean,
        repeatType: String?,
        isFlagged: Boolean,
        isProofRequired: Boolean,
        proofType: String?,) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var showRepeatOptions by remember { mutableStateOf(false) }
    var showCancelOptions by remember {mutableStateOf(false)}
    var isEdited by remember { mutableStateOf(false)}

    // Data variables
    var title by remember { mutableStateOf(initialTask.taskTitle) }
    var notes by remember { mutableStateOf(initialTask.taskNotes) }
    var isRepeatEnabled by remember { mutableStateOf(initialTask.taskIsRepeatEnabled) }
    var isFlagged by remember { mutableStateOf(initialTask.taskIsFlagged)}
    val isProofRequired = initialTask.taskIsProofRequired
    var deadline by remember { mutableStateOf<LocalDateTime?>(initialTask.taskDeadline) }
    var estimatedDuration by remember { mutableStateOf<Pair<Int, Int>?>(initialTask.taskEstimatedDuration) }
    var reminderTime by remember { mutableStateOf<LocalDateTime?>(initialTask.taskReminderTime) }
    var repeatType by remember { mutableStateOf<String?>(initialTask.taskRepeatType) }
    var proofType by remember {mutableStateOf(initialTask.taskProofType)}

    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Edit Task")
                    }
                },
                navigationIcon = {
                    TextButton(onClick = if (isEdited) {
                        {showCancelOptions = true}
                    }
                    else {onCancel}
                    ) {
                        Text("Cancel", color = LinkColor)
                    }
                },
                actions = {
                    TextButton(
                        onClick = if (isEdited)
                        { {
                            onSave(
                                title,
                                notes,
                                deadline,
                                estimatedDuration,
                                reminderTime,
                                isRepeatEnabled,
                                repeatType,
                                isFlagged,
                                isProofRequired,
                                proofType)
                        } }
                        else { {} },
                    ) {
                        Text(
                            "Save",
                            color = if (isEdited) LinkColor else Grey
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
        ) {
            Column (
                Modifier
                    .fillMaxSize()
                    .background(color = DarkGrey),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(start = 16.dp, end = 16.dp)
                        .background(PureWhite, RoundedCornerShape(10.dp)),
                ) {
                    CustomTextEditorWithoutIcon(
                        value = title,
                        onValueChange = {
                            title = it
                            isEdited = true },
                        isSingleLine = true,
                        height = 40,
                        placeholder = "Title"
                    )

                    HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                    CustomTextEditorWithoutIcon(
                        value = notes,
                        onValueChange = {
                            notes = it
                            isEdited= true },
                        isSingleLine = false,
                        height = 120,
                        placeholder = "Notes"
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .background(PureWhite, RoundedCornerShape(10.dp))
                ) {

                    DeadlinePickerRow(
                        onDeadlinePicked = { pickedDateTime ->
                            deadline = pickedDateTime
                        },
                        initialDateTime = deadline,
                        changeListener = { isEdited = true }
                    )

                    HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                    EstimatedDurationRow(
                        onDurationPicked = { h, m ->
                            estimatedDuration = h to m
                        },
                        initialDuration = estimatedDuration,
                        changeListener = { isEdited = true}
                    )

                    HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                    Box {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(45.dp)
                                .padding(start = 10.dp, end = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = DeepGreen)
                            Spacer(Modifier.width(12.dp))
                            Text("Repeat", fontSize = 16.sp, color = DarkGrey)
                            Spacer(Modifier.weight(1f))
                            repeatType?.let {
                                Box (
                                    Modifier
                                        .padding(end=20.dp)
                                ) {
                                    Text(repeatType!!, fontSize = 10.sp, color = DeepBlue)
                                }
                            }
                            Switch(
                                checked = isRepeatEnabled,
                                onCheckedChange = {
                                    isRepeatEnabled = it
                                    if (it) showRepeatOptions = true
                                    else repeatType = null
                                    isEdited = true


                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PureWhite,
                                    checkedTrackColor = OnColor,
                                    checkedBorderColor = Color.Transparent,
                                    uncheckedThumbColor = PureWhite,
                                    uncheckedTrackColor = VeryLightGrey,
                                    uncheckedBorderColor = Color.Transparent
                                )
                            )
                        }

                        if (showRepeatOptions) {
                            AlertDialog(
                                onDismissRequest = { /* Prevent dismissing on outside click */ },
                                title = { Text("") },
                                text = { Text("How often should this repeat?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            repeatType = "Weekly"
                                            showRepeatOptions = false
                                        },
                                    ) {
                                        Text("Weekly")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            repeatType = "Daily"
                                            showRepeatOptions = false
                                        },
                                    ) {
                                        Text("Daily")
                                    }
                                }
                            )
                        }

                        if (showCancelOptions) {
                            AlertDialog(
                                onDismissRequest = { showCancelOptions = false },
                                title = { Text("") },
                                text = { Text("Are you sure you want to discard changes?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showCancelOptions = false
                                        },
                                    ) {
                                        Text("NO")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showCancelOptions = false
                                            onCancel()
                                        }
                                    ) {
                                        Text("YES")
                                    }
                                }
                            )
                        }
                    }

                    if (deadline != null && estimatedDuration != null) {
                        reminderTime = deadline?.minusHours(estimatedDuration?.first?.toLong() ?: 0)
                            ?.minusMinutes(estimatedDuration?.second?.toLong() ?: 0)
                        val formatted = reminderTime?.format(DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a"))
                        HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)
                        Row(
                            Modifier
                                .height(45.dp)
                                .padding(start = 10.dp, end = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                "You will be reminded on $formatted",
                                fontSize = 12.sp,
                                color = DeepBlue,
                            )
                        }
                    }
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .background(PureWhite, RoundedCornerShape(10.dp)),
                ) {
                    Row(
                        Modifier
                            .height(45.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = DeepYellow)
                        Spacer(Modifier.width(12.dp))
                        Text("Flag", fontSize = 16.sp, color = DarkGrey)
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = isFlagged,
                            onCheckedChange = {
                                isFlagged = it
                                isEdited = true },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = OnColor,
                                checkedBorderColor = Color.Transparent,

                                uncheckedThumbColor = PureWhite,
                                uncheckedTrackColor = VeryLightGrey,
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                    Row(
                        Modifier
                            .padding(start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        if (isProofRequired) {
                            Text(proofType?:"", fontSize = 13.sp, color = DeepBlue)
                        }
                        else {
                            Text("No proof of completion required for this task", fontSize = 13.sp, color = DeepBlue)
                        }
                    }
                }
            }
        }
    }
}