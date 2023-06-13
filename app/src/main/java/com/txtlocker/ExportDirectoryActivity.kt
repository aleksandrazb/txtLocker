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

class ExportDirectoryActivity : AppCompatActivity() {

    private lateinit var currentDirectory: String
    private lateinit var secureOperation: SecureOperation
    private lateinit var chooseDirectoryList: List<String>
    private var chosenDirectory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_directory)

        //Get current directory from previous activity
        this.currentDirectory = intent.getSerializableExtra("CURRENT_DIRECTORY") as String

        //Get secureOperation from previous activity
        this.secureOperation = intent.getSerializableExtra("SECURE_OPERATION") as SecureOperation
        val notes = secureOperation.getNotes(currentDirectory)

        chooseDirectoryList = secureOperation.getAllDirectories()
        setupDirectoryList()

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            setupButtonBack()
        }

        val buttonExportDirectoryFinal = findViewById<Button>(R.id.buttonExportDirectoryFinal)
        buttonExportDirectoryFinal.setOnClickListener {
            setupButtonExportDirectory()
        }

    }

    private fun setupDirectoryList() {
        val spinner: Spinner = findViewById(R.id.chooseDirectory)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, chooseDirectoryList)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    chosenDirectory = chooseDirectoryList[position].toString()
                    Toast.makeText(this@ExportDirectoryActivity,
                        "Selected directory " +
                                "" + chooseDirectoryList[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(this@ExportDirectoryActivity,
                        "Choose directory", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun setupButtonExportDirectory() {
        val edittext_export_pin = findViewById<EditText>(R.id.editEncryptionExportPin)
        val exportPin = edittext_export_pin.text.toString()
        val exportSecureOperation = SecureOperation(exportPin)

        val saved = exportSecureOperation.saveExport(chosenDirectory, secureOperation.getNotes(chosenDirectory))

        if (saved) {
            Toast.makeText(applicationContext, "Saved exported directory in Documents", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                it.putExtra("SECURE_OPERATION", secureOperation)
            }
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(this@ExportDirectoryActivity,
                "Couldn't export directory", Toast.LENGTH_SHORT).show()
        }

    }
}