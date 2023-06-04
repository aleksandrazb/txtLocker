package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NotepadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)
        //TODO("Load note to the view for edit")
        //private lateinit var note: Note
        //private lateinit var itemTitle: EditText
        //private lateinit var itemNote: EditText

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClose = findViewById<Button>(R.id.buttonClose)

        findViewById<EditText>(R.id.editTextTitle)

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