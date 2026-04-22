package com.jnetai.bookwriter.ui.timer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.WritingSession
import com.jnetai.bookwriter.data.entity.DailyGoal
import com.jnetai.bookwriter.viewmodel.TimerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimerActivity : AppCompatActivity() {

    private var bookId: Long = -1
    private lateinit var viewModel: TimerViewModel

    private lateinit var tvTimer: TextView
    private lateinit var tvTotalTime: TextView
    private lateinit var tvStreak: TextView
    private lateinit var btnStartPause: Button
    private lateinit var btnStop: Button

    private var isRunning = false
    private var sessionStart: Long = 0
    private var elapsedSeconds: Long = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds = (System.currentTimeMillis() - sessionStart) / 1000
                updateTimerDisplay()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Writing Timer"

        bookId = intent.getLongExtra("BOOK_ID", -1)
        if (bookId == -1L) { finish(); return }

        viewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        tvTimer = findViewById(R.id.tvTimer)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        tvStreak = findViewById(R.id.tvStreak)
        btnStartPause = findViewById(R.id.btnStartPause)
        btnStop = findViewById(R.id.btnStop)

        loadStats()

        btnStartPause.setOnClickListener {
            if (isRunning) pause() else start()
        }

        btnStop.setOnClickListener { stop() }
    }

    private fun start() {
        isRunning = true
        sessionStart = System.currentTimeMillis() - (elapsedSeconds * 1000)
        btnStartPause.text = "Pause"
        handler.postDelayed(updateRunnable, 1000)
    }

    private fun pause() {
        isRunning = false
        btnStartPause.text = "Resume"
        handler.removeCallbacks(updateRunnable)
    }

    private fun stop() {
        if (elapsedSeconds > 0) {
            val session = WritingSession(
                bookId = bookId,
                startTime = sessionStart,
                endTime = System.currentTimeMillis(),
                durationSeconds = elapsedSeconds
            )
            viewModel.saveSession(session) {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                CoroutineScope(Dispatchers.IO).launch {
                    val existing = viewModel.getGoalForDate(bookId, today)
                    if (existing == null) {
                        viewModel.saveGoal(DailyGoal(bookId = bookId, date = today))
                    }
                }
                loadStats()
            }
        }
        isRunning = false
        elapsedSeconds = 0
        handler.removeCallbacks(updateRunnable)
        btnStartPause.text = "Start"
        updateTimerDisplay()
    }

    private fun updateTimerDisplay() {
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun loadStats() {
        CoroutineScope(Dispatchers.IO).launch {
            val totalTime = viewModel.getTotalTimeForBook(bookId)
            val streak = viewModel.getStreak(bookId)
            withContext(Dispatchers.Main) {
                val hours = totalTime / 3600
                val minutes = (totalTime % 3600) / 60
                tvTotalTime.text = "Total writing time: ${hours}h ${minutes}m"
                tvStreak.text = "Current streak: $streak day${if (streak != 1) "s" else ""}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}