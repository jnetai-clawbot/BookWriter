package com.jnetai.bookwriter.ui.chapter

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Chapter
import com.jnetai.bookwriter.viewmodel.ChapterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChapterEditActivity : AppCompatActivity() {

    private var chapterId: Long = -1
    private var bookId: Long = -1
    private lateinit var viewModel: ChapterViewModel
    private var currentChapter: Chapter? = null

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var etNotes: EditText
    private lateinit var tvWordCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter_edit)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chapterId = intent.getLongExtra("CHAPTER_ID", -1)
        bookId = intent.getLongExtra("BOOK_ID", -1)

        etTitle = findViewById(R.id.etChapterTitle)
        etContent = findViewById(R.id.etChapterContent)
        etNotes = findViewById(R.id.etChapterNotes)
        tvWordCount = findViewById(R.id.tvWordCount)

        viewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            if (chapterId != -1L) {
                val chapter = viewModel.getById(chapterId)
                chapter?.let {
                    currentChapter = it
                    lifecycleScope.launch(Dispatchers.Main) {
                        etTitle.setText(it.title)
                        etContent.setText(it.content)
                        etNotes.setText(it.notes)
                        updateWordCount()
                    }
                }
            }
        }

        etContent.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) { updateWordCount() }
        })
    }

    private fun updateWordCount() {
        val text = etContent.text.toString().trim()
        val words = if (text.isEmpty()) 0 else text.split("\\s+".toRegex()).size
        tvWordCount.text = "$words words"
    }

    override fun onPause() {
        super.onPause()
        saveChapter()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun saveChapter() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString()
        val notes = etNotes.text.toString()

        if (title.isEmpty() && content.isEmpty()) return

        val chapter = currentChapter?.copy(
            title = title.ifEmpty { "Untitled" },
            content = content,
            notes = notes,
            updatedAt = System.currentTimeMillis()
        ) ?: Chapter(
            bookId = bookId,
            title = title.ifEmpty { "Untitled" },
            content = content,
            notes = notes
        )

        viewModel.update(chapter)
    }
}