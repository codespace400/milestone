package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Transaction
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, targetDateEpochDay ASC")
    fun getAllGoalsWithMilestones(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY targetDateEpochDay ASC")
    fun getActiveGoals(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY completedDateEpochDay DESC, id DESC")
    fun getCompletedGoals(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :id")
    fun getGoalWithMilestonesById(id: Long): Flow<GoalWithMilestones?>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): Goal?

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun getGoalCount(): Int

    @Query("UPDATE goals SET isCompleted = :completed, completedDateEpochDay = :completedDate WHERE id = :id")
    suspend fun setGoalCompletionStatus(id: Long, completed: Boolean, completedDate: Long?)

    @Query("UPDATE goals SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}
