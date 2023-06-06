package com.txtlocker

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.txtlocker.Models.Note
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import kotlin.properties.Delegates

class ListOfNotesActivity : AppCompatActivity() {
    private var position by Delegates.notNull<Int>()
    private lateinit var file: String
    //private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: com.google.android.material.navigation.NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_notes)

        //----------------------------------------------------------------------------
        //TODO:Create navigation menu for directories
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        //val navigationView: NavigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar);
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.item -> Toast.makeText(applicationContext, "clicked", Toast.LENGTH_LONG).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        /*toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //DrawerToggle may not show up because NavigationIcon is not visible. You may need to call actionbar.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)
        //This is not working, no button to open menu
        navigationView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.item -> Toast.makeText(applicationContext, "clicked", Toast.LENGTH_LONG).show()
            }
            true
        }*/
        //----------------------------------------------------------------------------

        val buttonNewNote = findViewById<Button>(R.id.buttonNewNote)

        //Get array of saved notes
        //TODO:Get filename from previous activity (DONE?)
        file = intent.getSerializableExtra("FILE") as String

        val notes = loadNotesFromFile(file)
        val listViewNotes = findViewById<ListView>(R.id.listViewNotes)


        //Create a view of list of notes to choose
        listViewNotes.adapter = ListAdapter(this, notes)

        position = intent.getSerializableExtra("POSITION") as Int
        listViewNotes.setSelection(position)

        //Add a new note and scroll to the last one (the new note)
        buttonNewNote.setOnClickListener {
            notes.add(Note("New note's title", "New note"))
            saveNotesToFile(file, notes)

            val intent = Intent(this, NotepadActivity::class.java).also {
                it.putExtra("POSITION", notes.size - 1)
                it.putExtra("NOTES", notes)
                it.putExtra("FILE", file)
            }
            startActivity(intent)
            finish()

        }

        listViewNotes.setOnItemClickListener {
                parent, view, position, id ->

            val intent = Intent(this, NotepadActivity::class.java).also {
                it.putExtra("POSITION", position)
                it.putExtra("NOTES", notes)
                it.putExtra("FILE", file)
            }
            startActivity(intent)
            finish()
        }

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

    private fun loadNotesFromFile(fileName: String): ArrayList<Note> {
        var notes: ArrayList<Note> = ArrayList()
        val fileDirectory = applicationContext.filesDir
        val file = File(fileDirectory, fileName)

        val gson = Gson()

        return try {
            val fileReader = FileReader(file)

            val type: Type = object : TypeToken<ArrayList<Note>>() {}.type
            notes = gson.fromJson(fileReader, type)

            fileReader.close()
            notes
        }
        catch (e: IOException) {
            e.printStackTrace()
            notes
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

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }*/
}