package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edittext_pin = findViewById<EditText>(R.id.editTextPin)
        val button_submit = findViewById<Button>(R.id.buttonSubmit)
        val button_reset_app = findViewById<Button>(R.id.buttonResetApp)
        val currentDirectory = getString(R.string.main_note_storage)

        /*val themeSwitch = findViewById<Switch>(R.id.themeSwitch)
        themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val themeId = if (isChecked) {
                R.style.AppTheme_Dark
            } else {
                R.style.AppTheme_Light
            }
            setTheme(themeId)
            recreate() // Apply the new theme to the activity
        }*/

        button_submit.setOnClickListener {
            val secureOperation = SecureOperation(edittext_pin.text.toString())
            secureOperation.setContext(applicationContext)
            if (secureOperation.runAppDecryption()) {
                val intent = Intent(this, ListOfNotesActivity::class.java).also {
                    it.putExtra("POSITION", 0)
                    it.putExtra("CURRENT_DIRECTORY", currentDirectory)
                    it.putExtra("SECURE_OPERATION", secureOperation as Serializable)
                }
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(applicationContext, "Incorrect PIN", Toast.LENGTH_LONG).show()
            }


        }

        button_reset_app.setOnClickListener {
            val intent = Intent(this, ResetAppActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

