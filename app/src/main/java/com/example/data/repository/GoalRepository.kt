package com.example.data.repository

import com.example.data.local.GoalDao
import com.example.data.local.MilestoneDao
import com.example.data.local.populateInitialData
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.Milestone
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GoalRepository(
    private val goalDao: GoalDao,
    private val milestoneDao: MilestoneDao
) {
    val activeGoals: Flow<List<GoalWithMilestones>> = goalDao.getActiveGoals()
    val completedGoals: Flow<List<GoalWithMilestones>> = goalDao.getCompletedGoals()
    val allGoals: Flow<List<GoalWithMilestones>> = goalDao.getAllGoalsWithMilestones()

    fun getGoalWithMilestones(goalId: Long): Flow<GoalWithMilestones?> {
        return goalDao.getGoalWithMilestonesById(goalId)
    }

    suspend fun insertGoal(goal: Goal): Long {
        return goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun insertMilestone(milestone: Milestone): Long {
        return milestoneDao.insertMilestone(milestone)
    }

    suspend fun deleteMilestone(milestone: Milestone) {
        milestoneDao.deleteMilestone(milestone)
    }

    suspend fun toggleMilestone(milestoneId: Long, isDone: Boolean, goalId: Long) {
        val completedAt = if (isDone) System.currentTimeMillis() else null
        milestoneDao.toggleMilestoneStatus(milestoneId, isDone, completedAt)
    }

    suspend fun setGoalCompleted(goalId: Long, completed: Boolean) {
        val completedDate = if (completed) LocalDate.now().toEpochDay() else null
        goalDao.setGoalCompletionStatus(goalId, completed, completedDate)
    }

    suspend fun updateGoalNotes(goalId: Long, notes: String) {
        goalDao.updateNotes(goalId, notes)
    }

    suspend fun resetWithSampleData() {
        goalDao.deleteAllGoals()
        populateInitialData(goalDao, milestoneDao)
    }

    suspend fun clearAllData() {
        goalDao.deleteAllGoals()
    }
}
