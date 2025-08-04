package com.example.contactosmvvm_df.viewmodel
import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.model.GrupoConContactos
import com.example.contactosmvvm_df.repository.ContactosRepository
import kotlinx.coroutines.launch

enum class EstadoImportacion { VACIO, CARGANDO, EXITO, ERROR }

class ContactosViewModel(private val repository: ContactosRepository) : ViewModel() {
    private val _searchQuery = MutableLiveData<String>("")
    private val _estadoImportacion = MutableLiveData<EstadoImportacion>(EstadoImportacion.VACIO)
    val estadoImportacion: LiveData<EstadoImportacion> get() = _estadoImportacion

    val contactos: LiveData<List<Contacto>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.todosLosContactos
        } else {
            repository.buscarContactos(query)
        }
    }

    fun buscarContacto(query: String) {
        _searchQuery.value = query
    }

    fun eliminarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }

    fun getContactosParaExportar(): List<Contacto>? {
        return contactos.value
    }

    fun importarContactosDelDispositivo(contentResolver: ContentResolver) {
        viewModelScope.launch {
            _estadoImportacion.value = EstadoImportacion.CARGANDO
            try {
                repository.importarDesdeDispositivo(contentResolver)
                _estadoImportacion.value = EstadoImportacion.EXITO
            } catch (e: Exception) {
                _estadoImportacion.value = EstadoImportacion.ERROR
            }
        }
    }

    fun resetearEstadoImportacion() {
        _estadoImportacion.value = EstadoImportacion.VACIO
    }

    // Funciones para gestionar grupos
    fun insertarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.crearGrupo(grupo)
    }

    fun obtenerContactosDeGrupo(grupoId: Int): LiveData<GrupoConContactos> {
        return repository.obtenerGrupoConContactos(grupoId)
    }

    fun asociarContactoAGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.asociarContactoAGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }

    fun removerContactoDeGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.removerContactoDeGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }
}

class ContactosViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}