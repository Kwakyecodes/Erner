package com.example.erner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE taskIsCompleted = 0 ORDER BY taskDeadline ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE taskIsCompleted = 0 and date(taskDeadline / 1000, 'unixepoch') = date(:date / 1000, 'unixepoch') ORDER BY taskDeadline ASC")
    fun getTasksByDate(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE taskIsCompleted = 0 and taskIsFlagged = 1 ORDER BY taskDeadline ASC")
    fun getFlaggedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE taskIsCompleted = 1 ORDER BY taskDeadline ASC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET taskIsCompleted = NOT taskIsCompleted WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: Long)

    @Query("DELETE FROM tasks WHERE taskIsCompleted = 1")
    suspend fun deleteAllCompleted()
}
