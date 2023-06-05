package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.txtlocker.Models.Note
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.properties.Delegates

class NotepadActivity : AppCompatActivity() {
    private lateinit var file: String
    private var position by Delegates.notNull<Int>()
    private lateinit var notes: ArrayList<Note>
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        // Get the note object from the intent extras
        file = intent.getSerializableExtra("FILE") as String
        position = intent.getSerializableExtra("POSITION") as Int
        notes = intent.getSerializableExtra("NOTES") as ArrayList<Note>
        val note = notes[position]

        // Initialize the EditText fields
        noteTitle = findViewById(R.id.editTextTitle)
        //TODO: Change R.id.textInputNotes to R.id.editTextContent
        noteContent = findViewById(R.id.inputNotes) //in activity_notepad.xml it can be found under R.id.textInputNotes

        // Set the title and content of the note in the EditText fields
        noteTitle.setText(note.title)
        noteContent.setText(note.content)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClose = findViewById<Button>(R.id.buttonClose)

        buttonSave.setOnClickListener {
            note.title = noteTitle.text.toString()
            note.content = noteContent.text.toString()

            notes[position] = note

            saveNotesToFile(file, notes)

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", position)
                it.putExtra("FILE", file)
            }
            startActivity(intent)
            finish()
        }

        buttonDelete.setOnClickListener {
            notes.removeAt(position)

            saveNotesToFile(file, notes)

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", position-1)
                it.putExtra("FILE", file)
            }
            startActivity(intent)
            finish()
        }

        buttonClose.setOnClickListener {
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", position)
                it.putExtra("FILE", file)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun saveNotesToFile(fileName: String, notes: ArrayList<Note>) {

        try {
            val file = File(applicationContext.filesDir, fileName)

            val gson = Gson()
            val json = gson.toJson(notes)

            try {
                val fileWriter = FileWriter(file)
                fileWriter.write(json)
                fileWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }
}