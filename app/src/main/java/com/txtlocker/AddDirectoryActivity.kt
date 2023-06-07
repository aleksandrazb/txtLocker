package com.txtlocker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
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
    }

}