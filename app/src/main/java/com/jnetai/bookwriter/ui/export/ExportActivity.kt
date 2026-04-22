package com.jnetai.bookwriter.ui.export

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Book
import com.jnetai.bookwriter.viewmodel.BookViewModel
import com.jnetai.bookwriter.viewmodel.ChapterViewModel
import com.jnetai.bookwriter.viewmodel.CharacterViewModel
import com.jnetai.bookwriter.viewmodel.WorldbuildingViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ExportData(
    val title: String,
    val genre: String,
    val synopsis: String,
    val chapters: List<ChapterExport>,
    val characters: List<CharacterExport>,
    val worldbuilding: List<WorldExport>
)

data class ChapterExport(val title: String, val content: String, val notes: String)
data class CharacterExport(val name: String, val description: String, val notes: String, val relationships: String)
data class WorldExport(val title: String, val category: String, val content: String)

class ExportActivity : AppCompatActivity() {

    private var bookId: Long = -1
    private lateinit var bookViewModel: BookViewModel
    private lateinit var chapterViewModel: ChapterViewModel
    private lateinit var characterViewModel: CharacterViewModel
    private lateinit var worldbuildingViewModel: WorldbuildingViewModel

    private lateinit var rbText: RadioButton
    private lateinit var rbJson: RadioButton
    private lateinit var tvPreview: TextView
    private lateinit var btnExport: Button
    private lateinit var btnShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Export"

        bookId = intent.getLongExtra("BOOK_ID", -1)
        if (bookId == -1L) { finish(); return }

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        characterViewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)
        worldbuildingViewModel = ViewModelProvider(this).get(WorldbuildingViewModel::class.java)

        rbText = findViewById(R.id.rbPlainText)
        rbJson = findViewById(R.id.rbJson)
        tvPreview = findViewById(R.id.tvPreview)
        btnExport = findViewById(R.id.btnExport)
        btnShare = findViewById(R.id.btnShare)

        loadPreview()

        rbText.setOnClickListener { loadPreview() }
        rbJson.setOnClickListener { loadPreview() }

        btnExport.setOnClickListener { exportToFile() }

        btnShare.setOnClickListener { shareExport() }
    }

    private fun loadPreview() {
        lifecycleScope.launch(Dispatchers.IO) {
            val book = bookViewModel.getBook(bookId).value ?: return@launch
            val chapters = chapterViewModel.getChaptersForBook(bookId).value ?: emptyList()
            val characters = characterViewModel.getCharactersForBook(bookId).value ?: emptyList()
            val worldNotes = worldbuildingViewModel.getNotesForBook(bookId).value ?: emptyList()

            val exportData = ExportData(
                title = book.title,
                genre = book.genre,
                synopsis = book.synopsis,
                chapters = chapters.map { ChapterExport(it.title, it.content, it.notes) },
                characters = characters.map { CharacterExport(it.name, it.description, it.notes, it.relationships) },
                worldbuilding = worldNotes.map { WorldExport(it.title, it.category, it.content) }
            )

            val content = if (rbJson.isChecked) {
                GsonBuilder().setPrettyPrinting().create().toJson(exportData)
            } else {
                buildPlainText(exportData)
            }

            withContext(Dispatchers.Main) {
                tvPreview.text = content
            }
        }
    }

    private fun buildPlainText(data: ExportData): String {
        val sb = StringBuilder()
        sb.appendLine("# ${data.title}")
        sb.appendLine("Genre: ${data.genre}")
        sb.appendLine()
        sb.appendLine("## Synopsis")
        sb.appendLine(data.synopsis)
        sb.appendLine()

        data.chapters.forEach { ch ->
            sb.appendLine("## ${ch.title}")
            if (ch.notes.isNotEmpty()) sb.appendLine("[Notes: ${ch.notes}]")
            sb.appendLine(ch.content)
            sb.appendLine()
        }

        if (data.characters.isNotEmpty()) {
            sb.appendLine("## Characters")
            data.characters.forEach { c ->
                sb.appendLine("- ${c.name}: ${c.description}")
                if (c.relationships.isNotEmpty()) sb.appendLine("  Relationships: ${c.relationships}")
            }
            sb.appendLine()
        }

        if (data.worldbuilding.isNotEmpty()) {
            sb.appendLine("## World-Building")
            data.worldbuilding.forEach { w ->
                sb.appendLine("- [${w.category}] ${w.title}: ${w.content}")
            }
        }

        return sb.toString()
    }

    private fun exportToFile() {
        val content = tvPreview.text.toString()
        val ext = if (rbJson.isChecked) "json" else "txt"
        val fileName = "BookWriter_export.$ext"

        lifecycleScope.launch(Dispatchers.IO) {
            val file = File(getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), fileName)
            file.parentFile?.mkdirs()
            file.writeText(content)
            withContext(Dispatchers.Main) {
                tvPreview.text = "Saved to: ${file.absolutePath}\n\n$content"
            }
        }
    }

    private fun shareExport() {
        val content = tvPreview.text.toString()
        val ext = if (rbJson.isChecked) "json" else "txt"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = if (rbJson.isChecked) "application/json" else "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BookWriter Export")
        startActivity(Intent.createChooser(shareIntent, "Share export"))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}