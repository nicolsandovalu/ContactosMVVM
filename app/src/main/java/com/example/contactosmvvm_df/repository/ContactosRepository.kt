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


/**
 * Repositorio que maneja las operaciones de datos para los contactos y categorías.
 */
class ContactosRepository(
    private val contactoDao: ContactoDao,
    private val categoriaDao: CategoriaDao,
    private val grupoDao: GrupoDao
) {
    val todosLosContactos: LiveData<List<Contacto>> = contactoDao.obtenerContactos()
    val todasLasCategorias: LiveData<List<Categoria>> = categoriaDao.obtenerCategorias()
    val todosLosGrupos: LiveData<List<Grupo>> = grupoDao.obtenerGrupos()

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

    suspend fun insertarCategoria(categoria: Categoria) {
        categoriaDao.insertar(categoria)
    }

    suspend fun obtenerContactosParaBackup(): List<Contacto> {
        return contactoDao.getAllContactosForBackup()
    }

    suspend fun restaurarContactos(contactos: List<Contacto>) {
        contactos.forEach { contacto ->
            contactoDao.insertar(contacto)
        }
    }

    fun getContactoById(contactoId: Int): LiveData<Contacto> {
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
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val nombre = it.getString(nameIndex)
                val telefono = it.getString(numberIndex).replace("\\s".toRegex(), "")

                if (telefono.isNotBlank() && !telefonosExistentes.contains(telefono)) {
                    nuevosContactos.add(Contacto(nombre = nombre, telefono = telefono, email = null, categoriaId = 1))
                }
            }
        }

        if (nuevosContactos.isNotEmpty()) {
            contactoDao.insertarVarios(nuevosContactos)
        }
    }

    suspend fun crearGrupo(grupo: Grupo) {
        grupoDao.insertarGrupo(grupo)
    }

    fun obtenerGrupoConContactos(grupoId: Int): LiveData<GrupoConContactos> =
        grupoDao.obtenerGrupoConContactos(grupoId)

    fun obtenerContactoConGrupos(contactoId: Int): LiveData<ContactoConGrupos> =
        grupoDao.obtenerContactoConGrupos(contactoId)

    suspend fun asociarContactoAGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.insertarContactoGrupoCrossRef(crossRef)

    suspend fun removerContactoDeGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.eliminarContactoGrupoCrossRef(crossRef)

    suspend fun actualizarGruposDeContacto(contactoId: Int, nuevosGrupoIds: List<Int>) {
        grupoDao.eliminarTodasLasReferenciasDeContacto(contactoId)
        nuevosGrupoIds.forEach { grupoId ->
            grupoDao.insertarContactoGrupoCrossRef(ContactoGrupoCrossRef(contactoId, grupoId))
        }
    }
}