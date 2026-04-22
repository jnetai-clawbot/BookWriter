package com.jnetai.bookwriter.ui.book

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jnetai.bookwriter.BookWriterApp
import com.jnetai.bookwriter.BuildConfig
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.AppDatabase
import com.jnetai.bookwriter.data.entity.Book
import com.jnetai.bookwriter.ui.about.AboutActivity
import com.jnetai.bookwriter.viewmodel.BookViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BookViewModel
    private lateinit var adapter: BookAdapter
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "BookWriter"

        emptyView = findViewById(R.id.emptyView)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewBooks)
        adapter = BookAdapter { book -> openBookDetail(book) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        viewModel.allBooks.observe(this) { books ->
            adapter.submitList(books)
            emptyView.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
        }

        findViewById<FloatingActionButton>(R.id.fabAddBook).setOnClickListener {
            showNewBookDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewBookDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_book, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("New Book")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val title = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookTitle).text.toString().trim()
                val genre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookGenre).text.toString().trim()
                val synopsis = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBookSynopsis).text.toString().trim()
                val targetWords = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTargetWords).text.toString().toIntOrNull() ?: 80000

                if (title.isNotEmpty()) {
                    viewModel.insert(Book(title = title, genre = genre, synopsis = synopsis, targetWordCount = targetWords))
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("BOOK_ID", book.id)
        startActivity(intent)
    }
}