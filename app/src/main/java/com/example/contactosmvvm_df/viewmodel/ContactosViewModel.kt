package com.example.contactosmvvm_df.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactosmvvm_df.database.ContactosDatabase
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoConGrupos
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.model.GrupoConContactos
import com.example.contactosmvvm_df.repository.ContactosRepository
import kotlinx.coroutines.launch

class ContactosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactosRepository

    // Datos principales
    val contactos: LiveData<List<Contacto>>
    val categorias: LiveData<List<Categoria>>
    val grupos: LiveData<List<Grupo>>

    // Búsqueda
    private val _contactosFiltrados = MutableLiveData<List<Contacto>>()
    val contactosFiltrados: LiveData<List<Contacto>> = _contactosFiltrados
    private var ultimaBusqueda: String = ""

    init {
        val db = ContactosDatabase.getDatabase(application)
        repository = ContactosRepository(
            db.contactoDao(),
            db.categoriaDao(),
            db.grupoDao()
        )
        contactos = repository.contactos
        categorias = repository.categorias
        grupos = repository.grupos

        // Observar cambios en la lista completa para actualizar la búsqueda
        contactos.observeForever { listaContactos ->
            if (ultimaBusqueda.isNotEmpty()) {
                filtrarContactos(ultimaBusqueda, listaContactos)
            } else {
                _contactosFiltrados.postValue(listaContactos ?: emptyList())
            }
        }
    }

    // --- Operaciones con Contactos ---
    fun insertar(contacto: Contacto) = viewModelScope.launch {
        repository.insertarContacto(contacto)
    }

    fun actualizar(contacto: Contacto) = viewModelScope.launch {
        repository.actualizarContacto(contacto)
    }

    fun eliminarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }

    // --- Búsqueda ---
    fun buscar(query: String) {
        ultimaBusqueda = query
        if (query.isBlank()) {
            contactos.value?.let { _contactosFiltrados.postValue(it) }
        } else {
            viewModelScope.launch {
                val resultados = repository.buscarContactos(query)
                _contactosFiltrados.postValue(resultados.value ?: emptyList())
            }
        }
    }

    private fun filtrarContactos(query: String, contactos: List<Contacto>?) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _contactosFiltrados.postValue(contactos ?: emptyList())
            } else {
                val resultados = repository.buscarContactos(query)
                _contactosFiltrados.postValue(resultados.value ?: emptyList())
            }
        }
    }

    // --- Operaciones con Categorías ---
    fun insertarCategoria(categoria: Categoria) = viewModelScope.launch {
        repository.insertarCategoria(categoria)
    }

    // --- Operaciones con Grupos ---
    fun insertarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.insertarGrupo(grupo)
    }

    fun actualizarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.actualizarGrupo(grupo)
    }

    fun eliminarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.eliminarGrupo(grupo)
    }

    // --- Relaciones Contacto-Grupo ---
    fun asociarContactoAGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.asociarContactoAGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }

    fun removerContactoDeGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.removerContactoDeGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }

    // --- Consultas de relaciones ---
    fun obtenerContactosDeGrupo(grupoId: Int): LiveData<GrupoConContactos> {
        return repository.obtenerGrupoConContactos(grupoId)
    }

    fun obtenerGruposDeContacto(contactoId: Int): LiveData<ContactoConGrupos> {
        return repository.obtenerContactoConGrupos(contactoId)
    }

    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>> {
        return repository.obtenerTodosLosGruposConContactos()
    }

    // --- Consultas adicionales ---
    fun contarContactosEnGrupo(grupoId: Int, callback: (Int) -> Unit) = viewModelScope.launch {
        val count = repository.contarContactosEnGrupo(grupoId)
        callback(count)
    }
}