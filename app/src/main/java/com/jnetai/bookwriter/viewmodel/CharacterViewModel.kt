package com.jnetai.bookwriter.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.data.entity.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BookWriterApp).database
    private val characterDao = db.characterDao()

    fun getCharactersForBook(bookId: Long): LiveData<List<Character>> =
        characterDao.getCharactersForBook(bookId)

    fun insert(character: Character, callback: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = characterDao.insert(character)
            callback?.let { cb ->
                kotlinx.coroutines.withContext(Dispatchers.Main) { cb(id) }
            }
        }
    }

    fun update(character: Character) {
        viewModelScope.launch(Dispatchers.IO) { characterDao.update(character) }
    }

    fun delete(character: Character) {
        viewModelScope.launch(Dispatchers.IO) { characterDao.delete(character) }
    }

    suspend fun getById(id: Long): Character? {
        return kotlinx.coroutines.withContext(Dispatchers.IO) { characterDao.getById(id) }
    }
}