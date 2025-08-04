package com.example.contactosmvvm_df.utils

import com.example.contactosmvvm_df.model.Contacto

object VCardUtils {
    fun exportToVCard(contactos: List<Contacto>): String {
        val vcardBuilder = StringBuilder()
        for (contacto in contactos) {
            vcardBuilder.append("BEGIN:VCARD\n")
            vcardBuilder.append("VERSION:3.0\n")
            vcardBuilder.append("FN:${contacto.nombre}\n")
            if (contacto.telefono.isNotBlank()) {
                vcardBuilder.append("TEL;TYPE=CELL:${contacto.telefono}\n")
            }
            if (contacto.email != null && contacto.email.isNotBlank()) {
                vcardBuilder.append("EMAIL:${contacto.email}\n")
            }

            vcardBuilder.append("END:VCARD\n\n")
        }
        return vcardBuilder.toString()
    }
}