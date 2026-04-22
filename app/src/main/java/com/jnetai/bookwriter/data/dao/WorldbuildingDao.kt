package com.jnetai.bookwriter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jnetai.bookwriter.data.entity.WorldbuildingNote

@Dao
interface WorldbuildingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: WorldbuildingNote): Long

    @Update
    suspend fun update(note: WorldbuildingNote)

    @Delete
    suspend fun delete(note: WorldbuildingNote)

    @Query("SELECT * FROM worldbuilding WHERE bookId = :bookId ORDER BY category, title ASC")
    fun getNotesForBook(bookId: Long): LiveData<List<WorldbuildingNote>>

    @Query("SELECT * FROM worldbuilding WHERE id = :id")
    suspend fun getById(id: Long): WorldbuildingNote?
}