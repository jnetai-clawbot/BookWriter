package com.jnetai.bookwriter.ui.book

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.*
import com.jnetai.bookwriter.ui.chapter.ChapterEditActivity
import com.jnetai.bookwriter.ui.character.CharacterEditActivity
import com.jnetai.bookwriter.ui.timer.TimerActivity
import com.jnetai.bookwriter.ui.worldbuilding.WorldbuildingEditActivity
import com.jnetai.bookwriter.viewmodel.*

class BookDetailActivity : AppCompatActivity() {

    private var bookId: Long = -1
    private lateinit var bookViewModel: BookViewModel
    private lateinit var chapterViewModel: ChapterViewModel
    private lateinit var characterViewModel: CharacterViewModel
    private lateinit var worldbuildingViewModel: WorldbuildingViewModel
    private var currentBook: Book? = null

    // Views
    private lateinit var tvTitle: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvSynopsis: TextView
    private lateinit var tvWordCount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    // Adapters
    private lateinit var chapterAdapter: ChapterAdapter
    private lateinit var characterAdapter: CharacterAdapter
    private lateinit var worldbuildingAdapter: WorldbuildingAdapter

    private var currentTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        bookId = intent.getLongExtra("BOOK_ID", -1)
        if (bookId == -1L) { finish(); return }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        characterViewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
        worldbuildingViewModel = ViewModelProvider(this).get(WorldbuildingViewModel::class.java)

        tvTitle = findViewById(R.id.tvBookTitle)
        tvGenre = findViewById(R.id.tvBookGenre)
        tvSynopsis = findViewById(R.id.tvBookSynopsis)
        tvWordCount = findViewById(R.id.tvWordCount)
        progressBar = findViewById(R.id.progressBar)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd = findViewById(R.id.fabAdd)

        chapterAdapter = ChapterAdapter { chapter -> openChapter(chapter) }
        characterAdapter = CharacterAdapter { character -> openCharacter(character) }
        worldbuildingAdapter = WorldbuildingAdapter { note -> openWorldbuilding(note) }

        recyclerView.layoutManager = LinearLayoutManager(this)

        bookViewModel.getBook(bookId).observe(this) { book ->
            book?.let {
                currentBook = it
                tvTitle.text = it.title
                tvGenre.text = it.genre.ifEmpty { "No genre" }
                tvSynopsis.text = it.synopsis.ifEmpty { "No synopsis" }
                supportActionBar?.title = it.title
            }
        }

        chapterViewModel.getTotalWordCount(bookId).observe(this) { count ->
            val words = count ?: 0
            currentBook?.let { book ->
                val percent = if (book.targetWordCount > 0) (words * 100 / book.targetWordCount) else 0
                tvWordCount.text = "$words / ${book.targetWordCount} words ($percent%)"
                progressBar.progress = percent.coerceAtMost(100)
            }
        }

        setupTabs()
        showChaptersTab()

        fabAdd.setOnClickListener { onFabClick() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_book_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_timer -> {
                val intent = Intent(this, TimerActivity::class.java)
                intent.putExtra("BOOK_ID", bookId)
                startActivity(intent)
                true
            }
            R.id.action_edit_book -> {
                showEditBookDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Chapters"))
        tabLayout.addTab(tabLayout.newTab().setText("Characters"))
        tabLayout.addTab(tabLayout.newTab().setText("World"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentTab = tab.position
                when (tab.position) {
                    0 -> showChaptersTab()
                    1 -> showCharactersTab()
                    2 -> showWorldTab()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun showChaptersTab() {
        recyclerView.adapter = chapterAdapter
        chapterViewModel.getChaptersForBook(bookId).observe(this) { chapters ->
            chapterAdapter.submitList(chapters)
        }
    }

    private fun showCharactersTab() {
        recyclerView.adapter = characterAdapter
        characterViewModel.getCharactersForBook(bookId).observe(this) { chars ->
            characterAdapter.submitList(chars)
        }
    }

    private fun showWorldTab() {
        recyclerView.adapter = worldbuildingAdapter
        worldbuildingViewModel.getNotesForBook(bookId).observe(this) { notes ->
            worldbuildingAdapter.submitList(notes)
        }
    }

    private fun onFabClick() {
        when (currentTab) {
            0 -> showNewChapterDialog()
            1 -> showNewCharacterDialog()
            2 -> showNewWorldbuildingDialog()
        }
    }

    private fun showNewChapterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_chapter, null)
        AlertDialog.Builder(this)
            .setTitle("New Chapter")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etChapterTitle).text.toString().trim()
                if (title.isNotEmpty()) {
                    chapterViewModel.insert(Chapter(bookId = bookId, title = title))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNewCharacterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_character, null)
        AlertDialog.Builder(this)
            .setTitle("New Character")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCharName).text.toString().trim()
                if (name.isNotEmpty()) {
                    characterViewModel.insert(Character(bookId = bookId, name = name))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNewWorldbuildingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_worldbuilding, null)
        AlertDialog.Builder(this)
            .setTitle("New World Note")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etWorldTitle).text.toString().trim()
                val category = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etWorldCategory).text.toString().trim()
                if (title.isNotEmpty()) {
                    worldbuildingViewModel.insert(WorldbuildingNote(bookId = bookId, title = title, category = category))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openChapter(chapter: Chapter) {
        val intent = Intent(this, ChapterEditActivity::class.java)
        intent.putExtra("CHAPTER_ID", chapter.id)
        intent.putExtra("BOOK_ID", bookId)
        startActivity(intent)
    }

    private fun openCharacter(character: Character) {
        val intent = Intent(this, CharacterEditActivity::class.java)
        intent.putExtra("CHARACTER_ID", character.id)
        intent.putExtra("BOOK_ID", bookId)
        startActivity(intent)
    }

    private fun openWorldbuilding(note: WorldbuildingNote) {
        val intent = Intent(this, WorldbuildingEditActivity::class.java)
        intent.putExtra("WORLD_ID", note.id)
        intent.putExtra("BOOK_ID", bookId)
        startActivity(intent)
    }

    private fun showEditBookDialog() {
        currentBook?.let { book ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_new_book, null)
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookTitle).setText(book.title)
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookGenre).setText(book.genre)
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookSynopsis).setText(book.synopsis)
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTargetWords).setText(book.targetWordCount.toString())

            AlertDialog.Builder(this)
                .setTitle("Edit Book")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val title = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookTitle).text.toString().trim()
                    val genre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookGenre).text.toString().trim()
                    val synopsis = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookSynopsis).text.toString().trim()
                    val targetWords = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTargetWords).text.toString().toIntOrNull() ?: 80000

                    bookViewModel.update(book.copy(title = title, genre = genre, synopsis = synopsis, targetWordCount = targetWords, updatedAt = System.currentTimeMillis()))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}