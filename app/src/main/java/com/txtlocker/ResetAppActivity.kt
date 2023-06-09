package com.txtlocker

import SecureOperation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.txtlocker.Methods.StorageOperation

class ResetAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_app)

        val button_goBack = findViewById<Button>(R.id.buttonGoBack)
        button_goBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val edittext_newPin = findViewById<EditText>(R.id.editTextNewPin)
        val button_resetAppFinalize = findViewById<Button>(R.id.buttonResetAppFinalize)
        button_resetAppFinalize.setOnClickListener {
            var secureOperation = SecureOperation(edittext_newPin.text.toString())
            secureOperation.setContext(applicationContext)
            secureOperation.runAppFirstEncryption(secureOperation)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}