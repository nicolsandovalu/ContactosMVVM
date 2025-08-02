package com.example.contactosmvvm_df.repository

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
    suspend fun insertarContacto(contacto: Contacto): Long {
        return contactoDao.insertar(contacto)
    }
    suspend fun actualizarContacto(contacto: Contacto) = contactoDao.actualizar(contacto)
    suspend fun eliminarContacto(contacto: Contacto) = contactoDao.eliminar(contacto)

    // --- CRUD Categorías ---
    suspend fun insertarCategoria(categoria: Categoria) = categoriaDao.insertar(categoria)

    // --- CRUD Grupos ---
    suspend fun insertarGrupo(grupo: Grupo) = grupoDao.insertarGrupo(grupo)
    suspend fun actualizarGrupo(grupo: Grupo) = grupoDao.actualizarGrupo(grupo)
    suspend fun eliminarGrupo(grupo: Grupo) = grupoDao.eliminarGrupo(grupo)

    // --- Buscar contactos ---
    fun buscarContactos(query: String) = contactoDao.buscarContactos("%$query%")

    // --- Relaciones Contacto-Grupo ---
    suspend fun asociarContactoAGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.insertarContactoGrupoCrossRef(crossRef)

    suspend fun removerContactoDeGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.eliminarContactoGrupoCrossRef(crossRef)

    // --- Consultas de relaciones ---
    fun obtenerGrupoConContactos(grupoId: Int): LiveData<GrupoConContactos> =
        grupoDao.obtenerGrupoConContactos(grupoId)

    fun obtenerContactoConGrupos(contactoId: Int): LiveData<ContactoConGrupos> =
        grupoDao.obtenerContactoConGrupos(contactoId)

    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>> =
        grupoDao.obtenerTodosLosGruposConContactos()

    suspend fun contarContactosEnGrupo(grupoId: Int): Int =
        grupoDao.contarContactosEnGrupo(grupoId)

    // --- Funciones para AgregarContactoViewModel ---
    // Se corrige para que devuelva LiveData<Contacto> en lugar de LiveData<List<Contacto>>
    fun getContactoById(contactoId: Int): LiveData<Contacto> {
        return contactoDao.obtenerContactoPorId(contactoId)
    }

    suspend fun actualizarGruposDeContacto(contactoId: Int, grupoIds: List<Int>) {
        // Elimina las relaciones existentes
        val referenciasAEliminar = grupoDao.obtenerGruposDeContacto(contactoId).value?.map {
            ContactoGrupoCrossRef(contactoId, it.id)
        }
        referenciasAEliminar?.forEach {
            grupoDao.eliminarContactoGrupoCrossRef(it)
        }

        // Crea e inserta nuevas relaciones
        grupoIds.forEach { grupoId ->
            val nuevaReferencia = ContactoGrupoCrossRef(contactoId, grupoId)
            grupoDao.insertarContactoGrupoCrossRef(nuevaReferencia)
        }
    }
}