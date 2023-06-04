package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.txtlocker.Models.Note

class NotepadActivity : AppCompatActivity() {
    private lateinit var note: Note
    private lateinit var noteTitle: EditText
    private lateinit var noteNote: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Get the note object from the intent extras
        note = intent.getSerializableExtra("NOTE") as Note

        // Initialize the EditText fields
        noteTitle = findViewById(R.id.editTextTitle)
        noteNote = findViewById(R.id.inputNotes) //in activity_notepad.xml it can be found under R.id.textInputNotes

        // Set the title and content of the note in the EditText fields
        noteTitle.setText(note.title)
        noteNote.setText(note.note)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClose = findViewById<Button>(R.id.buttonClose)

        buttonSave.setOnClickListener {
            //TODO("Save note action")
            //note.title = itemTitle.text.toString()
            //note.note = itemNote.text.toString()
            val intent = Intent(this, ListOfNotesActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonDelete.setOnClickListener {
            //TODO("Delete note action")
            val intent = Intent(this, ListOfNotesActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonClose.setOnClickListener {
            //TODO("Close note action")
            val intent = Intent(this, ListOfNotesActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}