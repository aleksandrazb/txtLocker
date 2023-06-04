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

class ListOfNotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notes)

        //TODO("Get array of saved notes on account")
        val arrayNotes = arrayListOf<Note>(
            Note("Title1", "Note1"),
            Note("Title2", "Note2"),
            Note("Title3", "Note3"),
            Note("Title4", "Note4")
        )

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

}