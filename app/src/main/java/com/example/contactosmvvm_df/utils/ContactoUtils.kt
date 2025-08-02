package com.example.contactosmvvm_df.utils


import android.content.Context
import android.provider.ContactsContract
import com.example.contactosmvvm_df.model.Contacto

object ContactoUtils{

    fun obtenerContactosDispositivo(context: Context): List<Contacto> {
        val lista = mutableListOf<Contacto>()
        val resolver = context.contentResolver
        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val idxNombre = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val idxTelefono = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val nombre = it.getString(idxNombre) ?: continue
                val telefono = it.getString(idxTelefono) ?: continue
                lista.add(
                    Contacto(
                        nombre = nombre,
                        telefono = telefono,
                        email = "",
                        categoriaId = 1
                    )
                )
            }
        }

        return lista
    }
}
