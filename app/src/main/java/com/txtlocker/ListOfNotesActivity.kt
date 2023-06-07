package com.txtlocker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Note
import kotlin.properties.Delegates

class ListOfNotesActivity : AppCompatActivity() {
    private var position by Delegates.notNull<Int>()
    private lateinit var fileToOpen: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: com.google.android.material.navigation.NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notes)

        //Get filename from previous activity
        this.fileToOpen = intent.getSerializableExtra("FILE") as String

        val usedStorage = StorageOperation(applicationContext, fileToOpen)

        //Create navigation menu for directories
        this.drawerLayout = findViewById(R.id.drawerLayout)
        this.navigationView = findViewById(R.id.navigation_view)
        this.toolbar = findViewById(R.id.toolbar)
        this.toolbar.title = fileToOpen
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open, R.string.close)
        this.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupNavigationMenu(usedStorage)

        //Get notes from storage
        val notes = usedStorage.getNotesFromFile()

        //Load ListView with storage content
        loadListView(notes)

        val buttonNewNote = findViewById<Button>(R.id.buttonNewNote)
        buttonNewNote.setOnClickListener {
            setupButtonNewNote(usedStorage, notes)
        }

        //TODO:Create add a new directory function
        /*val buttonNewDirectory = findViewById<Button>(R.id.buttonNewDirectory)
        buttonNewDirectory.setOnClickListener {
            val countDirectories = jsonFiles?.size ?: 0
            Toast.makeText(applicationContext, "Creating directory $countDirectories", Toast.LENGTH_LONG).show()
            finish()

            val fileName = "$countDirectories.json"
            val file = File(applicationContext.filesDir, fileName)

            if(!file.exists()) {
                try {
                    val notes = arrayListOf<Note>(
                        Note("ExampleTitle1", "ExampleNote1"),
                        Note("ExampleTitle2", "ExampleNote2"),
                        Note("ExampleTitle3", "ExampleNote3"),
                        Note("ExampleTitle4", "ExampleNote4")
                    )
                    file.createNewFile()
                    val gson = Gson()
                    val json = gson.toJson(notes)

                    try {
                        val fileWriter = FileWriter(file)
                        fileWriter.write(json)
                        fileWriter.close()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // File content saved successfully
                }
                catch (e: IOException) {
                    // Error occurred while saving the file
                    e.printStackTrace()
                }
            }

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", "$countDirectories.json")
            }
            startActivity(intent)
            finish()
        }*/

        //----------------------------------------------------
        //TODO:Create drag&drop item order change

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
            positionRowNoteShort.text = notes[position].content
            return row
        }
    }

    private fun setupNavigationMenu(storage: StorageOperation) {
        val storages = storage.getListOfStorages()

        //Retrieve the reference to the navigation menu
        val menu = this.navigationView.menu

        //Clear existing menu items
        menu.clear()

        //Add menu items for each JSON file
        storages.forEachIndexed { index, file ->
            val menuItem = menu.add(file)
            menuItem.title = file // Set your own icon
        }

        //Add function to open each directory
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val itemName = menuItem.title.toString()

            menuItem.setOnMenuItemClickListener {
                runItem(itemName)
                this.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

    }

    private fun runItem(fileToOpen: String) {

        if (this.fileToOpen != fileToOpen) {
            Toast.makeText(applicationContext, fileToOpen, Toast.LENGTH_LONG).show()
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", fileToOpen)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun loadListView(notes: ArrayList<Note>) {
        //Create a view of list of notes to choose
        val listViewNotes = findViewById<ListView>(R.id.listViewNotes)
        listViewNotes.adapter = ListAdapter(this, notes)

        //Set view oon the chosen note position
        this.position = intent.getSerializableExtra("POSITION") as Int
        listViewNotes.setSelection(position)

        //Create action of editing clicked note
        listViewNotes.setOnItemClickListener {
                parent, view, position, id ->

            val intent = Intent(this, NotepadActivity::class.java).also {
                it.putExtra("POSITION", position)
                it.putExtra("NOTES", notes)
                it.putExtra("FILE", this.fileToOpen)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun setupButtonNewNote(storage: StorageOperation, notes: ArrayList<Note>) {
        notes.add(Note("New note's title", "New note"))
        storage.runSavingNotes(notes)

        val intent = Intent(this, NotepadActivity::class.java).also {
            it.putExtra("POSITION", notes.size - 1)
            it.putExtra("NOTES", notes)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }

}