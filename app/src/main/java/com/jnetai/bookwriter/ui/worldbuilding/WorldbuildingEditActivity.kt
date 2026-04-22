package com.jnetai.bookwriter.ui.worldbuilding

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.WorldbuildingNote
import com.jnetai.bookwriter.viewmodel.WorldbuildingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldbuildingEditActivity : AppCompatActivity() {

    private var worldId: Long = -1
    private var bookId: Long = -1
    private lateinit var viewModel: WorldbuildingViewModel
    private var currentNote: WorldbuildingNote? = null

    private lateinit var etTitle: EditText
    private lateinit var etCategory: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worldbuilding_edit)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        worldId = intent.getLongExtra("WORLD_ID", -1)
        bookId = intent.getLongExtra("BOOK_ID", -1)

        etTitle = findViewById(R.id.etWorldTitle)
        etCategory = findViewById(R.id.etWorldCategory)
        etContent = findViewById(R.id.etWorldContent)

        viewModel = ViewModelProvider(this).get(WorldbuildingViewModel::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            if (worldId != -1L) {
                val note = viewModel.getById(worldId)
                note?.let {
                    currentNote = it
                    lifecycleScope.launch(Dispatchers.Main) {
                        etTitle.setText(it.title)
                        etCategory.setText(it.category)
                        etContent.setText(it.content)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) return

        val note = currentNote?.copy(
            title = title,
            category = etCategory.text.toString(),
            content = etContent.text.toString()
        ) ?: WorldbuildingNote(
            bookId = bookId,
            title = title,
            category = etCategory.text.toString(),
            content = etContent.text.toString()
        )

        viewModel.update(note)
    }
}