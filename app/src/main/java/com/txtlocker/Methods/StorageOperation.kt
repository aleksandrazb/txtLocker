package com.txtlocker.Methods

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.txtlocker.Models.Directory
import com.txtlocker.Models.Note
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type

class StorageOperation(private var applicationContext: Context, private var fileName: String) {

    private val directory = applicationContext.filesDir
    private val fileFullName = "$fileName.json"
    private val file = File(directory, fileFullName)

    fun getListOfStorages(): MutableList<String> {
        val jsonFiles =
            directory.listFiles { _, name -> name.endsWith(".json") }

        val listOfStorages: MutableList<String> = mutableListOf()

        jsonFiles?.forEachIndexed { index, file ->
            listOfStorages.add(file.nameWithoutExtension)
        }
        return listOfStorages
    }

    fun getArrayListOfStorages(): ArrayList<Directory> {
        var arrayListOfDirectories: ArrayList<Directory> = ArrayList()
        val jsonFiles =
            directory.listFiles { _, name -> name.endsWith(".json") }

        jsonFiles?.forEachIndexed { index, file ->
            arrayListOfDirectories.add(Directory(file.nameWithoutExtension.toString()))
        }
        return arrayListOfDirectories
    }

    fun runCheckIfNotesStorageExist() {
        checkIfNotesStorageExist()
    }

    fun getNotesFromFile(): ArrayList<Note> {
        return loadNotesFromFile()
    }

    fun runSavingNotes(notes: ArrayList<Note>) {
        saveNotesToFile(notes)
    }

    private fun checkIfNotesStorageExist() {
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
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun loadNotesFromFile(): ArrayList<Note> {
        var notes: ArrayList<Note> = ArrayList()

        val gson = Gson()

        return try {
            val fileReader = FileReader(file)

            val type: Type = object : TypeToken<ArrayList<Note>>() {}.type
            notes = gson.fromJson(fileReader, type)

            fileReader.close()
            notes
        }
        catch (e: IOException) {
            e.printStackTrace()
            notes
        }
    }

    private fun saveNotesToFile(notes: ArrayList<Note>) {
        try {
            val gson = Gson()
            val json = gson.toJson(notes)

            try {
                val fileWriter = FileWriter(file)
                fileWriter.write(json)
                fileWriter.close()
            }
            catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "Couldn't write notes into the file", Toast.LENGTH_LONG).show()
            }

        }
        catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Couldn't save notes to file", Toast.LENGTH_LONG).show()
        }
    }

}
