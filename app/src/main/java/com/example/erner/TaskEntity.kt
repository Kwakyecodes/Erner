package com.example.erner
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String,
    val taskNotes: String,
    val taskDeadline: LocalDateTime?,
    val taskEstimatedDuration: Pair<Int, Int>?,
    val taskReminderTime: LocalDateTime?,
    val taskIsRepeatEnabled: Boolean,
    val taskRepeatType: String?,
    val taskIsFlagged: Boolean,
    var taskIsProofRequired: Boolean,
    var taskProofType: String?,
    val taskProjectName: String?,
    val taskIsCompleted: Boolean,
)