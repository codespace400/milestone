package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.time.LocalDate

data class GoalWithMilestones(
    @Embedded
    val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val milestones: List<Milestone> = emptyList()
) {
    val totalMilestones: Int get() = milestones.size
    val completedMilestones: Int get() = milestones.count { it.isDone }

    val progress: Float
        get() {
            if (goal.isCompleted) return 1.0f
            if (milestones.isEmpty()) return 0.0f
            return completedMilestones.toFloat() / totalMilestones.toFloat()
        }

    val progressPercentage: Int
        get() = (progress * 100).toInt().coerceIn(0, 100)

    val daysRemaining: Long
        get() {
            val today = LocalDate.now().toEpochDay()
            return goal.targetDateEpochDay - today
        }

    val isOverdue: Boolean
        get() = !goal.isCompleted && daysRemaining < 0
}
