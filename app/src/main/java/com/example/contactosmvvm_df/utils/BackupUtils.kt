package com.example.contactosmvvm_df.utils

import android.content.Context
import android.net.Uri
import com.example.contactosmvvm_df.model.Contacto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

object BackupUtils {
    fun escribirBackup(context: Context, contactos: List<Contacto>, uri: Uri): Boolean {
        val gson = Gson()
        val jsonString = gson.toJson(contactos)
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.writer().use {
                    it.write(jsonString)
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun restaurarDesdeBackup(context: Context, uri: Uri): List<Contacto>? {
        val gson = Gson()
        val listType = object : TypeToken<List<Contacto>>() {}.type

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    return gson.fromJson(reader, listType)
                }
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}