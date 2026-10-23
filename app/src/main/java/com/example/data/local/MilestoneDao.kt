package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Milestone
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Delete
    suspend fun deleteMilestone(milestone: Milestone)

    @Query("SELECT * FROM milestones WHERE goalId = :goalId ORDER BY isDone ASC, dueDateEpochDay ASC")
    fun getMilestonesForGoal(goalId: Long): Flow<List<Milestone>>

    @Query("UPDATE milestones SET isDone = :isDone, completedAt = :completedAt WHERE id = :id")
    suspend fun toggleMilestoneStatus(id: Long, isDone: Boolean, completedAt: Long?)

    @Query("DELETE FROM milestones WHERE goalId = :goalId")
    suspend fun deleteMilestonesForGoal(goalId: Long)
}
