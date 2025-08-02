package com.example.contactosmvvm_df.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.contactosmvvm_df.model.Contacto
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach

object BackupUtils {

    /**
     * Exporta la lista de contactos a un archivo de texto plano
     */
    fun exportar(context: Context, contactos: List<Contacto>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.getExternalFilesDir(null), "backup_contactos_$timestamp.txt")

        val contenido = contactos.joinToString("\n") { contacto ->
            "${contacto.nombre},${contacto.telefono},${contacto.email},${contacto.categoriaId}"
        }

        file.writeText(contenido)
        return file
    }

    /**
     * Importa contactos desde un archivo de texto plano
     */
    fun importar(context: Context, nombreArchivo: String = "backup_contactos.txt"): List<Contacto> {
        val file = File(context.getExternalFilesDir(null), nombreArchivo)
        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull { linea ->
            val datos = linea.split(",")
            if (datos.size >= 3) {
                Contacto(
                    nombre = datos[0].trim(),
                    telefono = datos[1].trim(),
                    email = datos[2].trim(),
                    categoriaId = if (datos.size > 3) datos[3].toIntOrNull() ?: 1 else 1
                )
            } else null
        }
    }

    /**
     * Exporta un contacto individual a formato vCard (.vcf)
     */
    fun exportarA_vCard(context: Context, contacto: Contacto): File {
        val vcfContent = """
            BEGIN:VCARD
            VERSION:3.0
            FN:${contacto.nombre}
            N:${contacto.nombre};;;
            TEL;TYPE=CELL:${contacto.telefono}
            EMAIL;TYPE=INTERNET:${contacto.email}
            END:VCARD
        """.trimIndent()

        val fileName = "${contacto.nombre.replace(" ", "_")}.vcf"
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(vcfContent)
        return file
    }

    /**
     * Exporta múltiples contactos a un archivo vCard
     */
    fun exportarContactosA_vCard(context: Context, contactos: List<Contacto>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.getExternalFilesDir(null), "contactos_$timestamp.vcf")

        val vcfContent = contactos.joinToString("\n") { contacto ->
            """
            BEGIN:VCARD
            VERSION:3.0
            FN:${contacto.nombre}
            N:${contacto.nombre};;;
            TEL;TYPE=CELL:${contacto.telefono}
            EMAIL;TYPE=INTERNET:${contacto.email}
            END:VCARD
            """.trimIndent()
        }

        file.writeText(vcfContent)
        return file
    }

    /**
     * Exporta contactos a formato CSV
     */
    fun exportarA_CSV(context: Context, contactos: List<Contacto>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.getExternalFilesDir(null), "contactos_$timestamp.csv")

        val csvContent = buildString {
            // Cabecera
            appendLine("Nombre,Teléfono,Email,Categoría")

            // Datos
            contactos.forEach { contacto ->
                appendLine("\"${contacto.nombre}\",\"${contacto.telefono}\",\"${contacto.email}\",${contacto.categoriaId}")
            }
        }

        file.writeText(csvContent)
        return file
    }

    /**
     * Importa contactos desde archivo CSV
     */
    fun importarDesde_CSV(context: Context, nombreArchivo: String): List<Contacto> {
        val file = File(context.getExternalFilesDir(null), nombreArchivo)
        if (!file.exists()) return emptyList()

        val lineas = file.readLines()
        if (lineas.isEmpty()) return emptyList()

        // Saltar la primera línea (cabecera)
        return lineas.drop(1).mapNotNull { linea ->
            try {
                val datos = parsearLineaCSV(linea)
                if (datos.size >= 3) {
                    Contacto(
                        nombre = datos[0].trim(),
                        telefono = datos[1].trim(),
                        email = datos[2].trim(),
                        categoriaId = if (datos.size > 3) datos[3].toIntOrNull() ?: 1 else 1
                    )
                } else null
            } catch (e: Exception) {
                null // Ignorar líneas con formato incorrecto
            }
        }
    }

    /**
     * Comparte un archivo de backup usando el sistema de compartir de Android
     */
    fun compartirArchivo(context: Context, archivo: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (archivo.extension.lowercase()) {
                    "vcf" -> "text/vcard"
                    "csv" -> "text/csv"
                    else -> "text/plain"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Backup de Contactos")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Compartir backup de contactos"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Obtiene la lista de archivos de backup disponibles
     */
    fun obtenerArchivosBackup(context: Context): List<File> {
        val directorioBackup = context.getExternalFilesDir(null)
        return directorioBackup?.listFiles { file ->
            file.name.contains("backup_contactos") ||
                    file.name.contains("contactos_") ||
                    file.extension in listOf("txt", "csv", "vcf")
        }?.toList() ?: emptyList()
    }

    /**
     * Elimina archivos de backup antiguos (más de 30 días)
     */
    fun limpiarBackupsAntiguos(context: Context) {
        val treintaDiasAtras = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)

        obtenerArchivosBackup(context).forEach { archivo ->
            if (archivo.lastModified() < treintaDiasAtras) {
                archivo.delete()
            }
        }
    }

    /**
     * Función auxiliar para parsear líneas CSV que pueden contener comas dentro de comillas
     */
    private fun parsearLineaCSV(linea: String): List<String> {
        val resultado = mutableListOf<String>()
        var dentroDeComillas = false
        var valorActual = StringBuilder()

        var i = 0
        while (i < linea.length) {
            val caracter = linea[i]

            when {
                caracter == '"' -> {
                    if (i + 1 < linea.length && linea[i + 1] == '"') {
                        // Comillas dobles escapadas
                        valorActual.append('"')
                        i++
                    } else {
                        // Cambiar estado de comillas
                        dentroDeComillas = !dentroDeComillas
                    }
                }
                caracter == ',' && !dentroDeComillas -> {
                    // Separador de campo
                    resultado.add(valorActual.toString())
                    valorActual.clear()
                }
                else -> {
                    valorActual.append(caracter)
                }
            }
            i++
        }

        // Agregar el último valor
        resultado.add(valorActual.toString())

        return resultado
    }
}