package com.jnetai.bookwriter.data.dao

import androidx.room.*
import com.jnetai.bookwriter.data.entity.WritingSession

@Dao
interface WritingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WritingSession): Long

    @Update
    suspend fun update(session: WritingSession)

    @Query("SELECT * FROM writing_sessions WHERE bookId = :bookId ORDER BY startTime DESC")
    suspend fun getSessionsForBook(bookId: Long): List<WritingSession>

    @Query("SELECT SUM(durationSeconds) FROM writing_sessions WHERE bookId = :bookId")
    suspend fun getTotalTimeForBook(bookId: Long): Long?
}