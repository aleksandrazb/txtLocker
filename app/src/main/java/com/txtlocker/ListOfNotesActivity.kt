package com.txtlocker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.txtlocker.Models.Note
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ListOfNotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notes)

        //Get array of saved notes
        val arrayNotes = loadNotesFromFile("storage.txt")
        val listViewNotes = findViewById<ListView>(R.id.listViewNotes)
        listViewNotes.adapter = ListAdapter(this, arrayNotes)

        listViewNotes.setOnItemClickListener {
                parent, view, position, id ->
            val note = arrayNotes[position]
            val intent = Intent(this, NotepadActivity::class.java).also {
                it.putExtra("NOTE", note)
            }
            startActivity(intent)
            finish()
        }
    }

    private class ListAdapter(context: Context, val notes:ArrayList<Note>): BaseAdapter() {
        private val mContext: Context = context

        //number of items on the list
        override fun getCount(): Int {
            return notes.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            //return super.getItemViewType(position)
            return "Test"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val textView = TextView(mContext)
//            textView.text = "This is a row"
//            return textView
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.list_row, viewGroup, false)
            val positionRowNoteTitle = row.findViewById<TextView>(R.id.textNoteTitle)
            positionRowNoteTitle.text = notes[position].title
            val positionRowNoteShort = row.findViewById<TextView>(R.id.textNoteShort)
            positionRowNoteShort.text = notes[position].note
            return row
        }
    }

    private fun loadNotesFromFile(fileName: String): ArrayList<Note> {
        val notes = arrayListOf<Note>()

        val fileDirectory = applicationContext.filesDir
        val file = File(fileDirectory, fileName)

        try {
            val lines = file.bufferedReader().readLines()

            var currentTitle: String? = null
            var currentNote: String? = null

            for (line in lines) {
                if (currentTitle == null) {
                    currentTitle = line
                }
                else if (currentNote == null) {
                    currentNote = line
                    notes.add(Note(currentTitle, currentNote))
                    currentTitle = null
                    currentNote = null
                }
            }
            return notes
        }
        catch (e: IOException) {
            e.printStackTrace()
            notes.add(Note("Error", "IOException"))
            return notes
        }
    }

}