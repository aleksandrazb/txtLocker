package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.txtlocker.Models.Note
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edittext_pin = findViewById<EditText>(R.id.editTextPin)
        val button_submit = findViewById<Button>(R.id.buttonSubmit)
        val button_reset_app = findViewById<Button>(R.id.buttonResetApp)

        button_submit.setOnClickListener {
            actionGivePermission(edittext_pin.text.toString())
        }

        button_reset_app.setOnClickListener {
            //TODO(Create resetting app functionality)
        }
    }

    private fun actionGivePermission(pin: String) {
        //TODO(Set up secure pin authentication)
        if (pin == "1234") {
            Toast.makeText(applicationContext, "Unlocked", Toast.LENGTH_LONG).show()
            finish()

            val fileName = "storage.json"
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

            //val intent = Intent(this, ListOfNotesActivity::class.java)
            //startActivity(intent)
            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", "storage.json")
            }
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(applicationContext, "Incorrect PIN", Toast.LENGTH_LONG).show()
        }
    }
}