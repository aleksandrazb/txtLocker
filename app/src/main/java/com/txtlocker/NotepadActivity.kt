package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import com.txtlocker.Models.Note
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.properties.Delegates

class NotepadActivity : AppCompatActivity() {
    private lateinit var file: String
    private var position by Delegates.notNull<Int>()
    private lateinit var notes: ArrayList<Note>
    private lateinit var noteTitle: EditText
    private lateinit var noteNote: EditText
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
        noteNote = findViewById(R.id.inputNotes) //in activity_notepad.xml it can be found under R.id.textInputNotes

        // Set the title and content of the note in the EditText fields
        noteTitle.setText(note.title)
        noteNote.setText(note.note)

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClose = findViewById<Button>(R.id.buttonClose)

        buttonSave.setOnClickListener {
            //TODO("Save note action")
            note.title = noteTitle.text.toString()
            note.note = noteNote.text.toString()

            notes[position] = note

            saveNotesToFile(file, notes)
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

    private fun saveNotesToFile(fileName: String, notes: ArrayList<Note>) {

        try {
            val file = File(applicationContext.filesDir, fileName)

            //val fileWriter = FileWriter(file)
            //val bufferedWriter = BufferedWriter(fileWriter)
            //for (note in notes) {
            //    bufferedWriter.write(note.title)
            //    bufferedWriter.newLine()
            //    bufferedWriter.write(note.note)
            //    bufferedWriter.newLine()
            //}
            //bufferedWriter.close()

            //fileWriter.close()

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