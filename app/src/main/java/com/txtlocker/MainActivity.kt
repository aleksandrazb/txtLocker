package com.txtlocker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.txtlocker.Methods.StorageOperation

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
            val fileToOpen = "storage"
            val storage = StorageOperation(applicationContext, fileToOpen)
            storage.runCheckIfNotesStorageExist()

            val intent = Intent(this, ListOfNotesActivity::class.java).also {
                it.putExtra("POSITION", 0)
                it.putExtra("FILE", fileToOpen)
            }
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(applicationContext, "Incorrect PIN", Toast.LENGTH_LONG).show()
        }
    }
}