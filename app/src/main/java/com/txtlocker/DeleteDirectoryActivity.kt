package com.txtlocker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory

class DeleteDirectoryActivity : AppCompatActivity() {
    private lateinit var fileToOpen: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_directory)

        this.fileToOpen = intent.getSerializableExtra("FILE") as String

        val usedStorage = StorageOperation(applicationContext, this.fileToOpen)

        //Get existing directories
        val notes = usedStorage.getNotesFromFile()

        loadListView(usedStorage.getArrayListOfStorages(), usedStorage)

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

    }

    private class ListAdapter(context: Context,
                              val directories: ArrayList<Directory>,
                              private val deleteClickListener: DirectoryDeleteClickListener
                              ): BaseAdapter() {
        private val mContext: Context = context

        interface DirectoryDeleteClickListener {
            fun onDirectoryDeleteClick(directory: Directory)
        }

        //number of items on the list
        override fun getCount(): Int {
            return directories.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return super.getItemViewType(position)
            //return "Test"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.list_row_delete_directory, viewGroup, false)
            val positionRowNoteTitle = row.findViewById<TextView>(R.id.textDirectoryName)
            positionRowNoteTitle.text = directories[position].name

            val deleteButton = row.findViewById<ImageButton>(R.id.buttonDeleteDirectory)
            deleteButton.setOnClickListener {
                val directory = directories[position]
                deleteClickListener.onDirectoryDeleteClick(directory)
            }

            return row
        }
    }

    private fun loadListView(directories: ArrayList<Directory>, storage: StorageOperation) {
        val listViewNotes = findViewById<ListView>(R.id.listViewDirectories)
        val listAdapter = ListAdapter(this, directories, object : ListAdapter.DirectoryDeleteClickListener {
            override fun onDirectoryDeleteClick(directory: Directory) {
                storage.deleteDirectory(directory.name)
                if (directory.name != getString(R.string.main_note_storage)) {
                    directories.remove(directory)
                }
                (listViewNotes.adapter as ListAdapter).notifyDataSetChanged()
            }
        })
        listViewNotes.adapter = listAdapter
    }

    private fun setupButtonBack() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", 0)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }

}