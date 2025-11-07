package com.example.erner

import android.content.Intent
import android.view.ViewTreeObserver
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
//import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.erner.ui.theme.DarkGrey
import com.example.erner.ui.theme.DeepBlue
import com.example.erner.ui.theme.DeepRed
import com.example.erner.ui.theme.DeepYellow
import com.example.erner.ui.theme.LightGrey
import com.example.erner.ui.theme.LinkColor
import com.example.erner.ui.theme.PureWhite
import com.example.erner.ui.theme.VeryLightGrey
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(activity: FragmentActivity, onSignOutClick: () -> Unit) {
    val viewModel: TaskViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(activity.application)
    )
    val navController = rememberNavController()
    val allTasks by viewModel.allTasks.collectAsState()
    val allTasksForToday by viewModel.allTasksForToday.collectAsState()
    val allFlaggedTasks by viewModel.allFlaggedTasks.collectAsState()
    val allCompletedTasks by viewModel.allCompletedTasks.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // val projects by viewModel.allProjectNames.collectAsState()

            var value by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            val isKeyboardOpen by keyboardAsState()
            LaunchedEffect(isKeyboardOpen) {
                if (!isKeyboardOpen) {
                    focusManager.clearFocus()
                }
            }

            Scaffold(
                topBar = {
                    var expanded by remember { mutableStateOf(false) }
                    TopAppBar(
                        title = { Text("Own Today") },
                        actions = {
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreHoriz, contentDescription = "Menu")
                                }

                                DropdownMenu(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    val context = LocalContext.current
                                    DropdownMenuItem(
                                        text = { Text("About") },
                                        onClick = {
                                            expanded = false
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                "https://www.linkedin.com/in/kwakye/".toUri()
                                            )
                                            context.startActivity(intent)
                                        }
                                    )

                                    HorizontalDivider(
                                        Modifier,
                                        thickness = 0.3.dp, color = VeryLightGrey
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Reports") },
                                        onClick = {
                                            expanded = false
                                            // TODO: Navigate to Reports
                                        }
                                    )

                                    HorizontalDivider(
                                        Modifier,
                                        thickness = 0.3.dp, color = VeryLightGrey
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Sign Out") },
                                        onClick = {
                                            expanded = false
                                            onSignOutClick()
                                        }
                                    )

                                    HorizontalDivider(
                                        Modifier,
                                        thickness = 0.3.dp, color = VeryLightGrey
                                    )

                                    DropdownMenuItem(
                                        text = { Text("Set App Limits") },
                                        onClick = {
                                            expanded = false
                                            // TODO: Navigate to Set App Limits
                                        }
                                    )
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .padding(WindowInsets.navigationBars.asPaddingValues()),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                AddTaskBottomSheet().show(
                                    activity.supportFragmentManager,
                                    "AddTaskBottomSheet"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(color = LinkColor, shape = CircleShape)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("New Task", color = LinkColor, fontWeight = FontWeight.Bold)
                        }
//                        Button(
//                            onClick = { /* No action */ },
//                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
//                        ) {
//                            Text("Add Project", color = LinkColor, fontWeight = FontWeight.Bold)
//                        }
                    }
                }
            ) { innerPadding ->
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
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize() // Important to give bounded size
                            .padding(16.dp)
                            .background(DarkGrey)
                    ) {
                        CustomTextEditorWithIcon(
                            value = value,
                            onValueChange = { value = it },
                            isSingleLine = true,
                            height = 40,
                            icon = Icons.Default.Search,
                            placeholder = "Search"
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            QuickBox(
                                Icons.Default.Today,
                                DeepBlue,
                                "Today",
                                allTasksForToday.size,
                                onClick = { navController.navigate("task_list/Today")}
                            )
                            QuickBox(
                                Icons.Default.CalendarMonth,
                                DeepRed,
                                "All",
                                allTasks.size,
                                onClick = { navController.navigate("task_list/All")}
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            QuickBox(
                                Icons.Default.CheckCircle,
                                LightGrey,
                                "Completed",
                                allCompletedTasks.size,
                                onClick = { navController.navigate("task_list/Completed")}
                            )
                            QuickBox(
                                Icons.Default.Flag,
                                DeepYellow,
                                "Flagged",
                                allFlaggedTasks.size,
                                onClick = { navController.navigate("task_list/Flagged")}
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        composable("task_list/{label}") { backStackEntry ->
            val label = backStackEntry.arguments?.getString("label") ?: "Tasks"

            val (tasks, titleColor) = when (label) {
                "All" -> allTasks to DeepRed
                "Flagged" -> allFlaggedTasks to DeepYellow
                "Completed" -> allCompletedTasks to LightGrey
                "Today" -> allTasksForToday to DeepBlue
                else -> emptyList<TaskEntity>() to LightGrey
            }
            TaskListScreen(
                viewModel = viewModel,
                title = label,
                titleColor = titleColor,
                tasks = tasks,
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
fun QuickBox(
    icon: ImageVector,
    hue : Color,
    label: String,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp, 80.dp)
            .clickable{ onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, contentDescription = label, tint=hue)
                Spacer(modifier = Modifier.weight(1f))
                Text(count.toString(), fontWeight = FontWeight.Bold)
            }
            Text(
                text=label,
                Modifier
                    .padding(start=7.dp),
            )
        }
    }
}

@Composable
fun CustomTextEditorWithIcon(
    value: String,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean,
    height: Int,
    icon: ImageVector,
    placeholder: String,
    ) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = DarkGrey,       // color of selection handles
        backgroundColor = LightGrey   // highlight color for selected text
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = isSingleLine,
            textStyle = TextStyle(fontSize = 14.sp, color = DarkGrey),
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
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LightGrey,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
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
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    val keyboardState = remember { mutableStateOf(false) }
    val viewTreeObserver = view.viewTreeObserver

    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            keyboardState.value = isKeyboardOpen
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return keyboardState
}
