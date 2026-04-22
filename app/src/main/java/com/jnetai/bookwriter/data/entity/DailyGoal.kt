package com.jnetai.bookwriter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val date: String,  // yyyy-MM-dd
    val targetWords: Int = 1000,
    val wordsWritten: Int = 0,
    val goalMet: Boolean = false
)