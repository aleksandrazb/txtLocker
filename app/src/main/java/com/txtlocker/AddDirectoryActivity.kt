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
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory
import com.txtlocker.Models.Note

class AddDirectoryActivity : AppCompatActivity() {
    private lateinit var fileToOpen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_directory)

        this.fileToOpen = intent.getSerializableExtra("FILE") as String

        val usedStorage = StorageOperation(applicationContext, this.fileToOpen)

        val buttonCreate = findViewById<Button>(R.id.buttonCreateDirectory)
        buttonCreate.setOnClickListener {
            setupButtonCreate(usedStorage.getListOfStorages(), usedStorage)
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

    }

    private fun setupButtonBack() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", 0)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()

    }

    private fun setupButtonCreate(existingFileNames: MutableList<String>, storage: StorageOperation) {
        val viewNewFileName = findViewById<EditText>(R.id.editTextDirectoryName)
        val newFileName = viewNewFileName.text.toString()

        if (existingFileNames.contains(newFileName)) {
            Toast.makeText(applicationContext, "Directory of this name already exist!", Toast.LENGTH_LONG).show()
        }
        else {
            storage.createNewStorage(newFileName)
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", newFileName)
            }
            startActivity(intent)
            finish()
        }
    }

}