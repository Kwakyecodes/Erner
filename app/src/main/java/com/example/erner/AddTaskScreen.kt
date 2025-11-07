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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.erner.ui.theme.DarkGrey
import com.example.erner.ui.theme.DeepBlue
import com.example.erner.ui.theme.DeepGreen
import com.example.erner.ui.theme.DeepRed
import com.example.erner.ui.theme.DeepYellow
import com.example.erner.ui.theme.LightGrey
import com.example.erner.ui.theme.LinkColor
import com.example.erner.ui.theme.OnColor
import com.example.erner.ui.theme.PureWhite
import com.example.erner.ui.theme.VeryLightGrey
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import com.example.erner.ui.theme.Grey
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onCancel: () -> Unit = {},
    onAdd: (
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
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isRepeatEnabled by remember { mutableStateOf(false) }
    var isFlagged by remember { mutableStateOf(false)}
    var isProofRequired by remember { mutableStateOf(false)}
    var deadline by remember { mutableStateOf<LocalDateTime?>(null) }
    var estimatedDuration by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var reminderTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var repeatType by remember { mutableStateOf<String?>(null) }
    var showRepeatOptions by remember { mutableStateOf(false) }
    var showCancelOptions by remember {mutableStateOf(false)}
    var proofType by remember {mutableStateOf(null)}

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
                        Text("New Task")
                    }
                },
                navigationIcon = {
                    val isEdited = title.isNotBlank() || notes.isNotBlank() ||
                            deadline!=null || estimatedDuration!=null
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
                        onClick = if (title.isNotBlank() && reminderTime != null)
                        { {
                            onAdd(
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
                            "Add",
                            color = if (title.isNotBlank() && reminderTime != null) LinkColor else Grey
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
                       onValueChange = { title = it },
                       isSingleLine = true,
                       height = 40,
                       placeholder = "Title"
                   )

                   HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                   CustomTextEditorWithoutIcon(
                       value = notes,
                       onValueChange = { notes = it },
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
                       initialDateTime = null,
                       changeListener = {},
                   )

                   HorizontalDivider(thickness = 0.4.dp, color = VeryLightGrey)

                   EstimatedDurationRow(
                       onDurationPicked = { h, m ->
                           estimatedDuration = h to m
                       },
                       initialDuration = null,
                       changeListener = {},
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
                           onCheckedChange = { isFlagged = it },
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
                           .height(60.dp)
                           .padding(start = 10.dp, end = 10.dp),
                       verticalAlignment = Alignment.CenterVertically,
                   ) {
                       Column (
                           verticalArrangement = Arrangement.spacedBy(0.01.dp)
                       ) {
                           Text("Require completion proof", fontSize = 16.sp, color = DarkGrey)
                           Text("This cannot be changed after task creation", fontSize = 9.sp, color = DeepRed)
                       }

                       Spacer(Modifier.weight(1f))
                       Switch(
                           checked = isProofRequired,
                           onCheckedChange = { isProofRequired = it },
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
               }
           }
        }
    }
}


@Composable
fun CustomTextEditorWithoutIcon(
    value: String,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean,
    height: Int,
    placeholder: String
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = DarkGrey,       // color of selection handles
        backgroundColor = LightGrey   // highlight color for selected text
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 15.sp, color = DarkGrey),
            singleLine = isSingleLine,
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .background(PureWhite, RoundedCornerShape(12.dp)),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (value.isEmpty()) {
                            Text(placeholder, fontSize = 14.sp, color = LightGrey)
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}

@Composable
fun DeadlinePickerRow(
    onDeadlinePicked: (LocalDateTime) -> Unit,
    initialDateTime: LocalDateTime?,
    changeListener: () -> Unit,
) {
    val context = LocalContext.current
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(initialDateTime) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(start = 10.dp, end = 10.dp)
            .clickable {
                // 1. Show date picker
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        // 2. Show time picker
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val dateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                                selectedDateTime = dateTime
                                onDeadlinePicked(dateTime)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
                changeListener()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = DeepRed)
        Spacer(Modifier.width(12.dp))
        Text("Set Deadline",
            fontSize = 16.sp,
            color = DarkGrey
        )
        selectedDateTime?.let {
            Spacer(Modifier.weight(1f))
            Text(
                it.format(DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a")),
                fontSize = 10.sp,
                color = DeepBlue)
        }
    }
}

@Composable
fun EstimatedDurationRow(
    onDurationPicked: (Int, Int) -> Unit,
    initialDuration: Pair<Int, Int>?,
    changeListener: () -> Unit,
) {
    val context = LocalContext.current
    var duration by remember { mutableStateOf<Pair<Int, Int>?>(initialDuration) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(start = 10.dp, end = 10.dp)
            .clickable {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        duration = hour to minute
                        onDurationPicked(hour, minute)
                    },
                    0, // default hour
                    0, // default minute
                    true
                ).show()
                changeListener()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Schedule, contentDescription = null, tint = DeepBlue)
        Spacer(Modifier.width(12.dp))
        Text("Set Estimated Duration", fontSize = 16.sp, color = DarkGrey)
        Spacer(Modifier.weight(1f))

        duration?.let { (h, m) ->
            Text(
                text = "${h}h ${m}m",
                fontSize = 10.sp,
                color = DeepBlue
            )
        }
    }
}