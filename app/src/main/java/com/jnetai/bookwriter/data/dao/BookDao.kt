package com.jnetai.bookwriter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jnetai.bookwriter.data.entity.Book

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM books ORDER BY updatedAt DESC")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: Long): Book?

    @Query("SELECT * FROM books WHERE id = :id")
    fun getByIdLive(id: Long): LiveData<Book?>
}