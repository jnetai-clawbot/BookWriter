package com.jnetai.bookwriter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jnetai.bookwriter.data.entity.Character

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Character): Long

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * FROM characters WHERE bookId = :bookId ORDER BY name ASC")
    fun getCharactersForBook(bookId: Long): LiveData<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getById(id: Long): Character?
}