package com.jnetai.bookwriter.ui.character

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jnetai.bookwriter.R
import com.jnetai.bookwriter.data.entity.Character
import com.jnetai.bookwriter.viewmodel.CharacterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterEditActivity : AppCompatActivity() {

    private var characterId: Long = -1
    private var bookId: Long = -1
    private lateinit var viewModel: CharacterViewModel
    private var currentCharacter: Character? = null

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etNotes: EditText
    private lateinit var etRelationships: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_edit)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        characterId = intent.getLongExtra("CHARACTER_ID", -1)
        bookId = intent.getLongExtra("BOOK_ID", -1)

        etName = findViewById(R.id.etCharName)
        etDescription = findViewById(R.id.etCharDescription)
        etNotes = findViewById(R.id.etCharNotes)
        etRelationships = findViewById(R.id.etCharRelationships)

        viewModel = ViewModelProvider(this).get(CharacterViewModel::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            if (characterId != -1L) {
                val character = viewModel.getById(characterId)
                character?.let {
                    currentCharacter = it
                    lifecycleScope.launch(Dispatchers.Main) {
                        etName.setText(it.name)
                        etDescription.setText(it.description)
                        etNotes.setText(it.notes)
                        etRelationships.setText(it.relationships)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveCharacter()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun saveCharacter() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) return

        val character = currentCharacter?.copy(
            name = name,
            description = etDescription.text.toString(),
            notes = etNotes.text.toString(),
            relationships = etRelationships.text.toString()
        ) ?: Character(
            bookId = bookId,
            name = name,
            description = etDescription.text.toString(),
            notes = etNotes.text.toString(),
            relationships = etRelationships.text.toString()
        )

        viewModel.update(character)
    }
}