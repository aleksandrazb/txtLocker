package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class AddDirectoryActivity : AppCompatActivity() {
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_directory)

        //Get opened directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation

        val buttonCreate = findViewById<Button>(R.id.buttonCreateDirectory)
        buttonCreate.setOnClickListener {
            setupButtonCreate(this.secureOperation)
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
    }

    private fun setupButtonCreate(secureOperation: SecureOperation) {
        val viewNewDirectoryName = findViewById<EditText>(R.id.editTextDirectoryName)
        val newDirectoryName = viewNewDirectoryName.text.toString()

        if (secureOperation.getAllDirectories().contains(newDirectoryName)) {
            Toast.makeText(applicationContext, "Directory of this name already exist!", Toast.LENGTH_LONG).show()
        }
        else {
            //TODO:Add function to add additionally encrypted directories
            secureOperation.addDirectory(newDirectoryName, false)
            secureOperation.runSaveAllDirectories()

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", newDirectoryName)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }
    }

}
