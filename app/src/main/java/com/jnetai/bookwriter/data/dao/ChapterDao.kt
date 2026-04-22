package com.jnetai.bookwriter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jnetai.bookwriter.data.entity.Chapter

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: Chapter): Long

    @Update
    suspend fun update(chapter: Chapter)

    @Delete
    suspend fun delete(chapter: Chapter)

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY orderIndex ASC")
    fun getChaptersForBook(bookId: Long): LiveData<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getById(id: Long): Chapter?

    @Query("SELECT SUM(LENGTH(content) - LENGTH(REPLACE(content, ' ', '')) + 1) FROM chapters WHERE bookId = :bookId")
    fun getTotalWordCount(bookId: Long): LiveData<Int?>
}