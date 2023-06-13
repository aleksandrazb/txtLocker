package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ExportMenuActivity : AppCompatActivity() {

    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_menu)

        //Get current directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        val notes = secureOperation.getNotes(currentDirectory)

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

        val buttonExportDirectory = findViewById<Button>(R.id.buttonExportDirectory)
        buttonExportDirectory.setOnClickListener {
            setupButtonExport()
        }

        val buttonImportDirectory = findViewById<Button>(R.id.buttonImport)
        buttonImportDirectory.setOnClickListener {
            setupButtonImport()
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

    private fun setupButtonExport() {
        val intent = Intent(this, ExportDirectoryActivity::class.java).also {
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }

    private fun setupButtonImport() {
        val intent = Intent(this, ListOfNotesActivity::class.java).also {
            it.putExtra("CURRENT_DIRECTORY", currentDirectory)
            it.putExtra("SECURE_OPERATION", secureOperation)
        }
        startActivity(intent)
        finish()
    }

}

