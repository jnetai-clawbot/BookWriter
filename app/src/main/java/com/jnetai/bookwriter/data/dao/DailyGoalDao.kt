package com.jnetai.bookwriter.data.dao

import androidx.room.*
import com.jnetai.bookwriter.data.entity.DailyGoal

@Dao
interface DailyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: DailyGoal): Long

    @Update
    suspend fun update(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE bookId = :bookId AND date = :date")
    suspend fun getForDate(bookId: Long, date: String): DailyGoal?

    @Query("SELECT * FROM daily_goals WHERE bookId = :bookId AND goalMet = 1 ORDER BY date DESC")
    suspend fun getStreakDays(bookId: Long): List<DailyGoal>

    @Query("SELECT * FROM daily_goals WHERE bookId = :bookId ORDER BY date DESC")
    suspend fun getAllForBook(bookId: Long): List<DailyGoal>
}