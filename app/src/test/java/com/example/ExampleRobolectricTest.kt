package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.Milestone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Milestone", appName)
  }

  @Test
  fun `goal progress calculation with milestones`() {
    val goal = Goal(id = 1, title = "Run Half Marathon")
    val milestones = listOf(
      Milestone(id = 10, goalId = 1, title = "Step 1", isDone = true),
      Milestone(id = 11, goalId = 1, title = "Step 2", isDone = true),
      Milestone(id = 12, goalId = 1, title = "Step 3", isDone = false),
      Milestone(id = 13, goalId = 1, title = "Step 4", isDone = false)
    )

    val goalWithMilestones = GoalWithMilestones(goal = goal, milestones = milestones)
    assertEquals(4, goalWithMilestones.totalMilestones)
    assertEquals(2, goalWithMilestones.completedMilestones)
    assertEquals(0.5f, goalWithMilestones.progress, 0.001f)
    assertEquals(50, goalWithMilestones.progressPercentage)
  }

  @Test
  fun `completed goal progress is always 100 percent`() {
    val goal = Goal(id = 2, title = "Master Meditation", isCompleted = true)
    val goalWithMilestones = GoalWithMilestones(goal = goal, milestones = emptyList())
    assertEquals(1.0f, goalWithMilestones.progress, 0.001f)
    assertEquals(100, goalWithMilestones.progressPercentage)
  }

  @Test
  fun `deadline countdown calculation`() {
    val targetEpoch = LocalDate.now().plusDays(32).toEpochDay()
    val goal = Goal(id = 3, title = "Launch App", targetDateEpochDay = targetEpoch)
    val goalWithMilestones = GoalWithMilestones(goal = goal)
    assertEquals(32L, goalWithMilestones.daysRemaining)
    assertFalse(goalWithMilestones.isOverdue)
  }
}
