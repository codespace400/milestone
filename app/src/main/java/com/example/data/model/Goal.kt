package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val emoji: String = "🎯",
    val targetDateEpochDay: Long = LocalDate.now().plusDays(30).toEpochDay(),
    val isCompleted: Boolean = false,
    val completedDateEpochDay: Long? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
