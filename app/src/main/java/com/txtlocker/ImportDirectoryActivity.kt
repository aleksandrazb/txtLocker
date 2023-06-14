package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class ImportDirectoryActivity : AppCompatActivity() {
    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_directory)

        //Get current directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        val notes = secureOperation.getNotes(currentDirectory)

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

        val buttonImportDirectoryFinal = findViewById<Button>(R.id.buttonImportDirectoryFinal)
        buttonImportDirectoryFinal.setOnClickListener {
            setupButtonImportDirectory()
        }

    }

    private fun setupButtonBack() {
        val intent = Intent(this, ExportMenuActivity::class.java).also {
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }

    private fun setupButtonImportDirectory() {
        val edittext_import_directory_name = findViewById<EditText>(R.id.editDirectoryImportedName)
        val exportName = edittext_import_directory_name.text.toString()
        val edittext_import_pin = findViewById<EditText>(R.id.editDecryptionImportPin)
        val exportPin = edittext_import_pin.text.toString()

        val saved = secureOperation.importDirectory("$exportName.export.txt", exportPin)


        if (saved) {
            secureOperation.runSaveAllDirectories()
            Toast.makeText(applicationContext, "Saved imported directory in $exportName directory", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(this@ImportDirectoryActivity,
                "Couldn't import directory", Toast.LENGTH_SHORT).show()
        }

    }
}