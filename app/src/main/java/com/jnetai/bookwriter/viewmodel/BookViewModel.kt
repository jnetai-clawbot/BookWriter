package com.jnetai.bookwriter.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.data.entity.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BookWriterApp).database
    private val bookDao = db.bookDao()

    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    fun insert(book: Book, callback: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = bookDao.insert(book)
            callback?.let { cb ->
                kotlinx.coroutines.withContext(Dispatchers.Main) { cb(id) }
            }
        }
    }

    fun update(book: Book) {
        viewModelScope.launch(Dispatchers.IO) { bookDao.update(book) }
    }

    fun delete(book: Book) {
        viewModelScope.launch(Dispatchers.IO) { bookDao.delete(book) }
    }

    fun getBook(id: Long): LiveData<Book?> = bookDao.getByIdLive(id)
}