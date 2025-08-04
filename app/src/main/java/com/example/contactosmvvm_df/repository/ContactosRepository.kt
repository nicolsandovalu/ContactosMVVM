package com.example.contactosmvvm_df.repository

import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import com.example.contactosmvvm_df.database.CategoriaDao
import com.example.contactosmvvm_df.database.ContactoDao
import com.example.contactosmvvm_df.database.GrupoDao
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoConGrupos
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.model.GrupoConContactos


class ContactosRepository(
    private val contactoDao: ContactoDao,
    private val categoriaDao: CategoriaDao,
    private val grupoDao: GrupoDao
) {

    val contactos = contactoDao.obtenerContactos()
    val categorias = categoriaDao.obtenerCategorias()
    val grupos = grupoDao.obtenerGrupos()

    // --- CRUD Contactos ---

    fun buscarContactos(query: String): LiveData<List<Contacto>> {
        return contactoDao.buscarContactos("%$query%")
    }
    suspend fun insertarContacto(contacto: Contacto): Long {
        return contactoDao.insertar(contacto)
    }
    suspend fun actualizarContacto(contacto: Contacto) {
        contactoDao.actualizar(contacto)
    }
    suspend fun eliminarContacto(contacto: Contacto) {
        contactoDao.eliminar(contacto)
    }

    // --- CRUD Categorías ---
    suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insertar(categoria)
    }

    suspend fun obtenerContactosParaBackup(): List<Contacto> {
        return contactoDao.getAllContactosForBackup()
    }

    suspend fun restaurarContactos(contacto: List<Contacto>) {
        contactos.forEach { contacto ->
            contactoDao.insertar(contacto)
        }
    }

    fun getContactoById(contactoId: Int) : LiveData<Contacto> {
        return contactoDao.obtenerContactoPorId(contactoId)
    }

    suspend fun importarDesdeDispositivo(contentResolver: ContentResolver) {
        val nuevosContactos = mutableListOf<Contacto>()
        val telefonosExistentes = contactoDao.getTodosComoLista().map { it.telefono }.toSet()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC "
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val nombre = it.getString(nameIndex)
                val telefono = it.getString(numberIndex).replace("\\s".toRegex(), "") //limpiar espacios

                // Evitar duplicados basados en el número de teléfono
                if (telefono.isNotBlank() && !telefonosExistentes.contains(telefono)) {
                    // Por defecto, se asigna a la categoría 1 (o la que se prefiera)
                    nuevosContactos.add(Contacto(nombre = nombre, telefono = telefono, email = "", categoriaId = 1))
                }
            }
        }

        if (nuevosContactos.isNotEmpty()){
            contactoDao.insertarVarios(nuevosContactos)
        }
    }



    // --- CRUD Grupos ---
    suspend fun insertarGrupo(grupo: Grupo) {
        grupoDao.insertarGrupo(grupo)
    }

    fun getGruposDeUnContacto(contactoId: Int): LiveData<ContactoConGrupos>{
        return grupoDao.obtenerContactoConGrupos(contactoId)
    }
    suspend fun actualizarGrupo(contactoId: Int, nuevosGruposId: List<Int>){
        grupoDao.eliminarTodasLasReferenciasDeContacto(contactoId)

        nuevosGruposId.forEach { grupoId ->
            grupoDao.insertarContactoGrupoCrossRef(ContactoGrupoCrossRef(contactoId, grupoId))
        }
    }

    fun obtenerGrupoConContactos(grupoId: Int) : LiveData<GrupoConContactos> =
        grupoDao.obtenerGrupoConContactos(grupoId)

    suspend fun asociarContactoAGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.insertarContactoGrupoCrossRef(crossRef)

    suspend fun removerContactoDeGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.eliminarContactoGrupoCrossRef(crossRef)

    suspend fun contarContactosEnGrupo(grupoId: Int): Int =
        grupoDao.contarContactosEnGrupo(grupoId)

    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>> =
        grupoDao.obtenerTodosLosGruposConContactos()
}
