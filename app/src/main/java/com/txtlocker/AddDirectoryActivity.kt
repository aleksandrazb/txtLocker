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
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.txtlocker.Methods.StorageOperation
import com.txtlocker.Models.Directory
import com.txtlocker.Models.Note

class AddDirectoryActivity : AppCompatActivity() {
    private lateinit var fileToOpen: String
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_directory)

        //this.fileToOpen = intent.getSerializableExtra("FILE") as String

        //Get opened directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation

        //val usedStorage = StorageOperation(applicationContext, this.fileToOpen)

        val buttonCreate = findViewById<Button>(R.id.buttonCreateDirectory)
        buttonCreate.setOnClickListener {
            //TODO:Create new setupButtonCreate()
            setupButtonCreate_NEW(this.secureOperation)
            //setupButtonCreate(usedStorage.getListOfStorages(), usedStorage)
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

    }

    private fun setupButtonBack() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", 0)
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()

        /*val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("POSITION", 0)
            it.putExtra("FILE", this.fileToOpen)
        }
        startActivity(intent)
        finish()*/

    }

    private fun setupButtonCreate_NEW(secureOperation: SecureOperation) {
        val viewNewFileName = findViewById<EditText>(R.id.editTextDirectoryName)
        val newDirectoryName = viewNewFileName.text.toString()

        if (secureOperation.getAllDirectories().contains(newDirectoryName)) {
            Toast.makeText(applicationContext, "Directory of this name already exist!", Toast.LENGTH_LONG).show()
        }
        else {
            //TODO:Add function to add additionally encrypted directories
            secureOperation.addDirectory(newDirectoryName, false)

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", newDirectoryName)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()

            /*val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", newDirectoryName)
            }
            startActivity(intent)
            finish()*/
        }
    }

    private fun setupButtonCreate(existingFileNames: MutableList<String>, storage: StorageOperation) {
        val viewNewFileName = findViewById<EditText>(R.id.editTextDirectoryName)
        val newFileName = viewNewFileName.text.toString()

        if (existingFileNames.contains(newFileName)) {
            Toast.makeText(applicationContext, "Directory of this name already exist!", Toast.LENGTH_LONG).show()
        }
        else {
            storage.createNewDirectory(newFileName)
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", newFileName)
            }
            startActivity(intent)
            finish()
        }
    }

}

private fun Parcelable.putExtra(s: String, secureOperation: SecureOperation) {

}