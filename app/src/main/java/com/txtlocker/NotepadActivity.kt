package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Note
import kotlin.properties.Delegates

class NotepadActivity : AppCompatActivity() {
    private lateinit var fileToOpen: String
    private var position by Delegates.notNull<Int>()
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Get the note object from the intent extras
        //this.fileToOpen = intent.getSerializableExtra("FILE") as String
        this.position = intent.getSerializableExtra("POSITION") as Int
        //this.notes = intent.getSerializableExtra("NOTES") as ArrayList<Note>
        //Get opened directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        val notes = secureOperation.getNotes(currentDirectory)
        //val note = notes[position]
        //val note = this.notes[position]

        //Setup storage
        //val usedStorage = StorageOperation(applicationContext, fileToOpen)

        // Initialize the EditText fields
        this.noteTitle = findViewById(R.id.editTextTitle)
        this.noteContent = findViewById(R.id.inputNotes)

        // Set the title and content of the note in the EditText fields
        this.noteTitle.setText(notes[position].title)
        this.noteContent.setText(notes[position].content)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            //TODO:Create new setupButtonSave()
            setupButtonSave_NEW(this.currentDirectory, notes)
            //setupButtonSave(usedStorage, note)
        }

        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            //TODO:Create new setupButtonDelete()
            setupButtonDelete_NEW(this.currentDirectory, notes)
            //setupButtonDelete(usedStorage)
        }

        val buttonClose = findViewById<Button>(R.id.buttonClose)
        buttonClose.setOnClickListener {
            setupButtonClose()
        }

    }

    private fun setupButtonSave_NEW(directoryName: String, notes: ArrayList<Note>) {
        notes[position].title = noteTitle.text.toString()
        notes[position].content = noteContent.text.toString()

        //this.notes[position] = note

        secureOperation.saveChangedNotes(directoryName, notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()

    }

    /*
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

    }*/

    private fun setupButtonDelete_NEW(directoryName: String, notes: ArrayList<Note>) {
        notes.removeAt(this.position)
        secureOperation.saveChangedNotes(directoryName, notes)
        //storage.runSavingNotes(this.notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position -1)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()

    }

    /*
    private fun setupButtonDelete(storage: StorageOperation) {
        this.notes.removeAt(this.position)
        storage.runSavingNotes(this.notes)

        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", this.position -1)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }*/

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

private fun Parcelable.putExtra(s: String, secureOperation: SecureOperation) {

}