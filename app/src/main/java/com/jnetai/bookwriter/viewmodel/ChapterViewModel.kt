package com.jnetai.bookwriter.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.data.entity.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BookWriterApp).database
    private val chapterDao = db.chapterDao()

    fun getChaptersForBook(bookId: Long): LiveData<List<Chapter>> =
        chapterDao.getChaptersForBook(bookId)

    fun getTotalWordCount(bookId: Long): LiveData<Int?> =
        chapterDao.getTotalWordCount(bookId)

    fun insert(chapter: Chapter, callback: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = chapterDao.insert(chapter)
            callback?.let { cb ->
                kotlinx.coroutines.withContext(Dispatchers.Main) { cb(id) }
            }
        }
    }

    fun update(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) { chapterDao.update(chapter) }
    }

    fun delete(chapter: Chapter) {
        viewModelScope.launch(Dispatchers.IO) { chapterDao.delete(chapter) }
    }

    suspend fun getById(id: Long): Chapter? {
        return kotlinx.coroutines.withContext(Dispatchers.IO) { chapterDao.getById(id) }
    }
}