package com.example.erner

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    val allTasks = dao.getAllTasks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val today = System.currentTimeMillis()
    val allTasksForToday = dao.getTasksByDate(today).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allFlaggedTasks = dao.getFlaggedTasks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allCompletedTasks = dao.getCompletedTasks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            dao.toggleTaskCompletion(taskId)
        }
    }

    var selectedTask by mutableStateOf<TaskEntity?>(null)
        private set
    fun selectTask(task: TaskEntity) { selectedTask = task }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            dao.deleteAllCompleted()
        }
    }

}
