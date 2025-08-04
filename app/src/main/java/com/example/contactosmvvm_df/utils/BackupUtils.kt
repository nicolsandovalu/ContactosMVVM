package com.example.contactosmvvm_df.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.contactosmvvm_df.model.Contacto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach

object BackupUtils {

    /**
     * Escribe la lista de contactos en formato JSON en la URI proporcionada.
     */
    fun escribirBackup(context: Context, contactos: List<Contacto>, uri: Uri): Boolean {
        val gson = Gson()
        val jsonString = gson.toJson(contactos)
        try {
            // Usa el ContentResolver para abrir un flujo de salida a la URI seleccionada por el usuario
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.writer().use {
                    it.write(jsonString)
                }
            }
            return true
        } catch (e: Exception) {
            // Manejar errores de escritura de archivo
            e.printStackTrace()
            return false
        }
    }

    /**
     * Lee un archivo de backup desde una URI y lo convierte en una lista de contactos.
     */
    fun restaurarDesdeBackup(context: Context, uri: Uri): List<Contacto>? {
        val gson = Gson()
        // Define el tipo de dato esperado para la deserialización (una lista de Contacto)
        val listType = object : TypeToken<List<Contacto>>() {}.type

        try {
            // Usa el ContentResolver para abrir un flujo de entrada desde la URI seleccionada
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    return gson.fromJson(reader, listType)
                }
            }
            return null
        } catch (e: Exception) {
            // Manejar errores de lectura o formato JSON incorrecto
            e.printStackTrace()
            return null
        }
    }
}
