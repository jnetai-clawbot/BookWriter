package com.jnetai.bookwriter.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.data.entity.WorldbuildingNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldbuildingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BookWriterApp).database
    private val worldbuildingDao = db.worldbuildingDao()

    fun getNotesForBook(bookId: Long): LiveData<List<WorldbuildingNote>> =
        worldbuildingDao.getNotesForBook(bookId)

    fun insert(note: WorldbuildingNote, callback: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = worldbuildingDao.insert(note)
            callback?.let { cb ->
                kotlinx.coroutines.withContext(Dispatchers.Main) { cb(id) }
            }
        }
    }

    fun update(note: WorldbuildingNote) {
        viewModelScope.launch(Dispatchers.IO) { worldbuildingDao.update(note) }
    }

    fun delete(note: WorldbuildingNote) {
        viewModelScope.launch(Dispatchers.IO) { worldbuildingDao.delete(note) }
    }

    suspend fun getById(id: Long): WorldbuildingNote? {
        return kotlinx.coroutines.withContext(Dispatchers.IO) { worldbuildingDao.getById(id) }
    }
}