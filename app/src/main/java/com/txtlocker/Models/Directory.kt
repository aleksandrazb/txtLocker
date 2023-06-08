package com.txtlocker.Models

data class Directory(var name:String, var encrypted: Boolean, var notes: ArrayList<Note>):java.io.Serializable
