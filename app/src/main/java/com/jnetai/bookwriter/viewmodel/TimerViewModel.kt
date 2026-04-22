package com.jnetai.bookwriter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.data.dao.WritingSessionDao
import com.jnetai.bookwriter.data.dao.DailyGoalDao
import com.jnetai.bookwriter.data.entity.WritingSession
import com.jnetai.bookwriter.data.entity.DailyGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as BookWriterApp).database
    private val sessionDao: WritingSessionDao = db.writingSessionDao()
    private val goalDao: DailyGoalDao = db.dailyGoalDao()

    fun saveSession(session: WritingSession, callback: ((Long) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = sessionDao.insert(session)
            callback?.let { cb ->
                kotlinx.coroutines.withContext(Dispatchers.Main) { cb(id) }
            }
        }
    }

    suspend fun getTotalTimeForBook(bookId: Long): Long {
        return withContext(Dispatchers.IO) { sessionDao.getTotalTimeForBook(bookId) ?: 0L }
    }

    fun saveGoal(goal: DailyGoal) {
        viewModelScope.launch(Dispatchers.IO) { goalDao.insert(goal) }
    }

    suspend fun getStreak(bookId: Long): Int {
        return withContext(Dispatchers.IO) {
            goalDao.getStreakDays(bookId).size
        }
    }

    suspend fun getGoalForDate(bookId: Long, date: String): DailyGoal? {
        return withContext(Dispatchers.IO) { goalDao.getForDate(bookId, date) }
    }
}