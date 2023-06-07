package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Note
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.properties.Delegates

class NotepadActivity : AppCompatActivity() {
    private lateinit var fileToOpen: String
    private var position by Delegates.notNull<Int>()
    private lateinit var notes: ArrayList<Note>
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Get the note object from the intent extras
        this.fileToOpen = intent.getSerializableExtra("FILE") as String
        this.position = intent.getSerializableExtra("POSITION") as Int
        this.notes = intent.getSerializableExtra("NOTES") as ArrayList<Note>
        val note = this.notes[position]

        //Setup storage
        val usedStorage = StorageOperation(applicationContext, fileToOpen)

        // Initialize the EditText fields
        this.noteTitle = findViewById(R.id.editTextTitle)
        this.noteContent = findViewById(R.id.inputNotes)

        // Set the title and content of the note in the EditText fields
        this.noteTitle.setText(note.title)
        this.noteContent.setText(note.content)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            setupButtonSave(usedStorage, note)
        }

        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            setupButtonDelete(usedStorage)
        }

        val buttonClose = findViewById<Button>(R.id.buttonClose)
        buttonClose.setOnClickListener {
            setupButtonClose()
        }

    }

    private fun setupButtonSave(storage: StorageOperation, note: Note) {
        note.title = noteTitle.text.toString()
        note.content = noteContent.text.toString()

        this.notes[position] = note

        storage.runSavingNotes(this.notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }

    private fun setupButtonDelete(storage: StorageOperation) {
        this.notes.removeAt(this.position)
        storage.runSavingNotes(this.notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position -1)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }

    private fun setupButtonClose() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()
        
    }
}