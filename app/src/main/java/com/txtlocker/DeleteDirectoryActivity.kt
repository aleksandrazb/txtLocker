package com.txtlocker

import SecureOperation
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.txtlocker.Models.Directory

class DeleteDirectoryActivity : AppCompatActivity() {
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_directory)

        //Get opened directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation


        //Get existing directories
        loadListView()

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

    }

    private class ListAdapter(context: Context,
                              val directories: MutableList<String>,//ArrayList<Directory>,
                              val secureOperation: SecureOperation,
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
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.list_row_delete_directory, viewGroup, false)
            val positionRowNoteTitle = row.findViewById<TextView>(R.id.textDirectoryName)
            positionRowNoteTitle.text = directories[position]

            val deleteButton = row.findViewById<ImageButton>(R.id.buttonDeleteDirectory)
            deleteButton.setOnClickListener {
                val directory: Directory? = secureOperation.getDirectory(directories[position])
                if (directory != null) {
                    deleteClickListener.onDirectoryDeleteClick(directory)
                }

            }
            return row
        }
    }

    private fun loadListView() {
        val listViewDirectories = findViewById<ListView>(R.id.listViewDirectories)
        val directories = this.secureOperation.getAllDirectories()
        val listAdapter = ListAdapter(this, directories, this.secureOperation, object : ListAdapter.DirectoryDeleteClickListener {
            override fun onDirectoryDeleteClick(directory: Directory) {
                if (directory.name != getString(R.string.main_note_storage))
                {
                    val deleted = secureOperation.deleteDirectory(directory.name)
                    if (deleted) {
                        Toast.makeText(
                            applicationContext,
                            "Directory ${directory.name} deleted",
                            Toast.LENGTH_LONG
                        ).show()
                        directories.remove(directory.name) // Remove the directory from the list
                        (listViewDirectories.adapter as ListAdapter).notifyDataSetChanged() // Notify the adapter of the data change
                    }
                }
                else
                {
                    Toast.makeText(
                        applicationContext,
                        "Main directory can't be removed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        listViewDirectories.adapter = listAdapter
    }

    private fun setupButtonBack() {
        if (currentDirectory in secureOperation.getAllDirectories())
        {
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", applicationContext.getString(R.string.main_note_storage))
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }

    }

}
