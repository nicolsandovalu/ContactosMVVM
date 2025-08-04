package com.example.contactosmvvm_df.utils

import com.example.contactosmvvm_df.model.Contacto


/**
 * Objeto de utilidad para exportar contactos al formato VCard (.vcf).
 */
object VCardUtils {

    /**
     * Convierte una lista de contactos a un único string en formato VCard.
     * @param contactos La lista de contactos a exportar.
     * @return Un String que contiene todos los contactos en formato .vcf.
     */
    fun exportToVCard(contactos: List<Contacto>): String {
        val vcardBuilder = StringBuilder()
        for (contacto in contactos) {
            vcardBuilder.append("BEGIN:VCARD\n")
            vcardBuilder.append("VERSION:3.0\n")

            // Nombre formateado (obligatorio)
            vcardBuilder.append("FN:${contacto.nombre}\n")

            // Teléfono
            if (contacto.telefono.isNotBlank()) {
                vcardBuilder.append("TEL;TYPE=CELL:${contacto.telefono}\n")
            }

            // Email
            if (contacto.email != null) {
                if (contacto.email.isNotBlank()) {
                    vcardBuilder.append("EMAIL:${contacto.email}\n")
                }
            }

            vcardBuilder.append("END:VCARD\n\n") // Doble salto de línea para separar entradas
        }
        return vcardBuilder.toString()
    }
}