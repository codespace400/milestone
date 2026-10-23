package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Goal
import com.example.data.model.Milestone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [Goal::class, Milestone::class],
    version = 1,
    exportSchema = false
)
abstract class MilestoneDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun milestoneDao(): MilestoneDao

    companion object {
        @Volatile
        private var INSTANCE: MilestoneDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MilestoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilestoneDatabase::class.java,
                    "milestone_database"
                )
                    .addCallback(MilestoneDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class MilestoneDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.goalDao(), database.milestoneDao())
                }
            }
        }
    }
}

suspend fun populateInitialData(goalDao: GoalDao, milestoneDao: MilestoneDao) {
    val today = LocalDate.now()

    // Goal 1: Run a Half Marathon
    val goal1Id = goalDao.insertGoal(
        Goal(
            title = "Run a Half Marathon (21 km)",
            description = "Build endurance and cross the finish line in under 2 hours.",
            category = "Fitness",
            emoji = "🏃‍♂️",
            targetDateEpochDay = today.plusDays(32).toEpochDay(),
            notes = "Weekly long run on Sundays. Hydrate properly with electrolytes!"
        )
    )
    milestoneDao.insertMilestones(
        listOf(
            Milestone(goalId = goal1Id, title = "Complete 5 km non-stop", dueDateEpochDay = today.minusDays(14).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 14 * 86400000L),
            Milestone(goalId = goal1Id, title = "Run 10 km under 60 mins", dueDateEpochDay = today.minusDays(4).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 4 * 86400000L),
            Milestone(goalId = goal1Id, title = "Finish 15 km endurance run", dueDateEpochDay = today.plusDays(10).toEpochDay(), isDone = false),
            Milestone(goalId = goal1Id, title = "Race day: 21.1 km finish line!", dueDateEpochDay = today.plusDays(32).toEpochDay(), isDone = false)
        )
    )

    // Goal 2: Launch Android App
    val goal2Id = goalDao.insertGoal(
        Goal(
            title = "Launch Milestone on Google Play",
            description = "Design and ship a polished, modern Jetpack Compose application.",
            category = "Career",
            emoji = "🚀",
            targetDateEpochDay = today.plusDays(18).toEpochDay(),
            notes = "Focus on micro-interactions, spring animations, and edge-to-edge support."
        )
    )
    milestoneDao.insertMilestones(
        listOf(
            Milestone(goalId = goal2Id, title = "Design UI system & custom palette", dueDateEpochDay = today.minusDays(5).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 5 * 86400000L),
            Milestone(goalId = goal2Id, title = "Implement Room persistence & DAOs", dueDateEpochDay = today.minusDays(1).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 1 * 86400000L),
            Milestone(goalId = goal2Id, title = "Polish animations and progress rings", dueDateEpochDay = today.plusDays(6).toEpochDay(), isDone = false),
            Milestone(goalId = goal2Id, title = "Release APK & Google Play asset kit", dueDateEpochDay = today.plusDays(18).toEpochDay(), isDone = false)
        )
    )

    // Goal 3: Read 12 Books This Year
    val goal3Id = goalDao.insertGoal(
        Goal(
            title = "Read 12 Impactful Books",
            description = "Expand perspectives on philosophy, habits, design, and economics.",
            category = "Learning",
            emoji = "📚",
            targetDateEpochDay = today.plusDays(75).toEpochDay(),
            notes = "Take 3 key takeaways from every book into personal notes."
        )
    )
    milestoneDao.insertMilestones(
        listOf(
            Milestone(goalId = goal3Id, title = "Read 'Atomic Habits' by James Clear", dueDateEpochDay = today.minusDays(30).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 30 * 86400000L),
            Milestone(goalId = goal3Id, title = "Read 'Deep Work' by Cal Newport", dueDateEpochDay = today.minusDays(10).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 10 * 86400000L),
            Milestone(goalId = goal3Id, title = "Read 'Thinking, Fast and Slow'", dueDateEpochDay = today.plusDays(25).toEpochDay(), isDone = false),
            Milestone(goalId = goal3Id, title = "Read 'The Psychology of Money'", dueDateEpochDay = today.plusDays(60).toEpochDay(), isDone = false)
        )
    )

    // Goal 4: Completed Goal - Master 30-Day Morning Meditation
    val goal4Id = goalDao.insertGoal(
        Goal(
            title = "Master 30-Day Morning Meditation",
            description = "Practice 15 minutes of mindfulness every morning before looking at screens.",
            category = "Personal",
            emoji = "🧘",
            targetDateEpochDay = today.minusDays(3).toEpochDay(),
            isCompleted = true,
            completedDateEpochDay = today.minusDays(3).toEpochDay(),
            notes = "Experienced noticeably reduced stress and clearer focus throughout the day!"
        )
    )
    milestoneDao.insertMilestones(
        listOf(
            Milestone(goalId = goal4Id, title = "Complete week 1 breathwork", dueDateEpochDay = today.minusDays(24).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 24 * 86400000L),
            Milestone(goalId = goal4Id, title = "Complete week 2 body scans", dueDateEpochDay = today.minusDays(17).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 17 * 86400000L),
            Milestone(goalId = goal4Id, title = "Complete week 3 unguided focus", dueDateEpochDay = today.minusDays(10).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 10 * 86400000L),
            Milestone(goalId = goal4Id, title = "30-day streak unlocked!", dueDateEpochDay = today.minusDays(3).toEpochDay(), isDone = true, completedAt = System.currentTimeMillis() - 3 * 86400000L)
        )
    )
}
