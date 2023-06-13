package com.txtlocker

import SecureOperation
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.txtlocker.Models.Note
import java.io.Serializable
import kotlin.properties.Delegates

//TODO:Handle secureOperation from MainActivity (and other activities)
class ListOfNotesActivity : AppCompatActivity() {
    private var position by Delegates.notNull<Int>()
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: com.google.android.material.navigation.NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notes)

        //Get position on the list from previous activity
        this.position = intent.getSerializableExtra("POSITION") as Int

        //Get opened directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        secureOperation.setContext(applicationContext)

        //Create navigation menu for directories
        this.drawerLayout = findViewById(R.id.drawerLayout)
        this.navigationView = findViewById(R.id.navigation_view)
        this.toolbar = findViewById(R.id.toolbar)
        this.toolbar.title = currentDirectory

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open, R.string.close)
        this.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationMenu()

        //Load ListView with directory content
        loadListView()

        val buttonNewNote = findViewById<Button>(R.id.buttonNewNote)
        buttonNewNote.setOnClickListener {
            setupButtonNewNote()
        }
        //buttonNewNote.elevation = 0F; // Set the elevation to 0 to remove the shadow
        //buttonNewNote.background = null; // Set the background to null to remove any background drawable

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
            return super.getItemViewType(position)
            //return "Test"
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

    private fun setupNavigationMenu() {
        val directories = secureOperation.getAllDirectories()

        //Retrieve the reference to the navigation menu
        val menu = this.navigationView.menu

        //Clear existing menu items
        menu.clear()

        //Add menu items for each JSON file
        directories.forEachIndexed { index, directoryName ->
            val menuItem = menu.add(directoryName)
            menuItem.title = directoryName // Set your own icon
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

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        val headerView = navigationView.getHeaderView(0) // Get the first (and usually only) header view

        //Add function to add directory
        val buttonAddDirectory = headerView.findViewById<ImageButton>(R.id.buttonAddDirectory)
        buttonAddDirectory.setOnClickListener {
            val intent = Intent(this, AddDirectoryActivity::class.java).also {
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }

        //Add function to delete directory
        val buttonDeleteDirectory = headerView.findViewById<ImageButton>(R.id.buttonDeleteDirectory)
        buttonDeleteDirectory.setOnClickListener {
            val intent = Intent(this, DeleteDirectoryActivity::class.java).also {
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }

        //Add function to go to export directory menu
        val buttonExport = headerView.findViewById<ImageButton>(R.id.buttonExport)
        buttonExport.setOnClickListener {
            //TODO:Create export directory view link
            val intent = Intent(this, ExportMenuActivity::class.java).also {
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }

        //Add function to exit app
        val buttonExit = headerView.findViewById<Button>(R.id.buttonExitApp)
        buttonExit.setOnClickListener {
            secureOperation.runSaveAllDirectories()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun runItem(directoryToOpen: String) {

        if (this.currentDirectory != directoryToOpen) {
            Toast.makeText(applicationContext, directoryToOpen, Toast.LENGTH_LONG).show()
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", directoryToOpen)
                it.putExtra("SECURE_OPERATION", secureOperation as Serializable)
            }
            startActivity(intent)
            finish()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadListView() {
        val notes = this.secureOperation.getNotes(this.currentDirectory)
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
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }
        var adapter = ListAdapter(this, notes)
        listViewNotes.adapter = adapter
        enableChangeOfOrderOfNotes(listViewNotes, notes, adapter, secureOperation)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun enableChangeOfOrderOfNotes(listView: ListView, notes: ArrayList<Note>, adapter: ListAdapter, secureOperation: SecureOperation) {
        var draggedItemIndex: Int = -1
        listView.setOnItemLongClickListener { parent, view, position, id ->
            draggedItemIndex = position
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(data, shadowBuilder, view, 0)
        }

        listView.setOnDragListener { _, event ->
            val action = event.action
            when (action) {
                DragEvent.ACTION_DROP -> {
                    val draggedItem = event.localState as View
                    val position = listView.pointToPosition(event.x.toInt(), event.y.toInt())

                    if (position != ListView.INVALID_POSITION) {
                        val droppedItemIndex = position
                        val droppedNote = notes[draggedItemIndex]

                        notes.removeAt(draggedItemIndex)
                        notes.add(droppedItemIndex, droppedNote)
                        adapter.notifyDataSetChanged()
                        secureOperation.saveChangedNotes(currentDirectory, notes)
                    }

                    draggedItem.visibility = View.VISIBLE
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    val draggedItem = event.localState as View
                    draggedItem.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    private fun setupButtonNewNote() {
        var notes = secureOperation.getNotes(currentDirectory)
        notes.add(Note("New note's title", "New note"))
        secureOperation.saveChangedNotes(currentDirectory, notes)

        val intent = Intent(this, NotepadActivity::class.java).also {
            it.putExtra("POSITION", notes.size - 1)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()

    }

}