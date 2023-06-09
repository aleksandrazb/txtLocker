package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.txtlocker.Models.Note
import kotlin.properties.Delegates

class NotepadActivity : AppCompatActivity() {
    private var position by Delegates.notNull<Int>()
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Get the note position in directory
        this.position = intent.getSerializableExtra("POSITION") as Int

        //Get current directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        val notes = secureOperation.getNotes(currentDirectory)

        // Initialize the EditText fields
        this.noteTitle = findViewById(R.id.editTextTitle)
        this.noteContent = findViewById(R.id.inputNotes)

        // Set the title and content of the note in the EditText fields
        this.noteTitle.setText(notes[position].title)
        this.noteContent.setText(notes[position].content)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            setupButtonSave(this.currentDirectory, notes)
        }

        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            setupButtonDelete(this.currentDirectory, notes)
        }

        val buttonClose = findViewById<Button>(R.id.buttonClose)
        buttonClose.setOnClickListener {
            setupButtonClose()
        }
    }

    private fun setupButtonSave(directoryName: String, notes: ArrayList<Note>) {
        notes[position].title = noteTitle.text.toString()
        notes[position].content = noteContent.text.toString()

        secureOperation.saveChangedNotes(directoryName, notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }

    private fun setupButtonDelete(directoryName: String, notes: ArrayList<Note>) {
        notes.removeAt(this.position)
        secureOperation.saveChangedNotes(directoryName, notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position -1)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }

    private fun setupButtonClose() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }
}
