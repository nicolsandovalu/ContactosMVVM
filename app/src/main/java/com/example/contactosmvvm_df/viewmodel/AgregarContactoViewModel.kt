package com.example.contactosmvvm_df.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoConGrupos
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.repository.ContactosRepository
import kotlinx.coroutines.launch

// ViewModel para la pantalla de agregar o editar un contacto.
class AgregarContactoViewModel(private val repository: ContactosRepository) : ViewModel() {

    val todosLosGrupos: LiveData<List<Grupo>> = repository.todosLosGrupos
    val todasLasCategorias: LiveData<List<Categoria>> = repository.todasLasCategorias
    var gruposDelContacto: LiveData<ContactoConGrupos> = MutableLiveData()

    private val _estadoGuardado = MutableLiveData<Result<Unit>>()
    val estadoGuardado: LiveData<Result<Unit>> = _estadoGuardado

    suspend fun insertarContacto(contacto: Contacto): Long {
        return repository.insertarContacto(contacto)
    }

    fun actualizarContacto(contacto: Contacto) = viewModelScope.launch {
        try {
            repository.actualizarContacto(contacto)
            _estadoGuardado.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    fun getContactoById(contactoId: Int): LiveData<Contacto> {
        gruposDelContacto = repository.obtenerContactoConGrupos(contactoId)
        return repository.getContactoById(contactoId)
    }

    fun guardarContactoYAsociarGrupos(contacto: Contacto, grupoIds: List<Int>) = viewModelScope.launch {
        try {
            if (contacto.id == 0) {
                val nuevoId = insertarContacto(contacto)
                repository.actualizarGruposDeContacto(nuevoId.toInt(), grupoIds)
                _estadoGuardado.postValue(Result.success(Unit))
            } else {
                repository.actualizarContacto(contacto)
                repository.actualizarGruposDeContacto(contacto.id, grupoIds)
                _estadoGuardado.postValue(Result.success(Unit))
            }
        } catch (e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    // Método faltante para crear un nuevo grupo
    fun crearNuevoGrupo(nombreGrupo: String) = viewModelScope.launch {
        if (nombreGrupo.isNotBlank()) {
            val nuevoGrupo = Grupo(nombre = nombreGrupo)
            repository.crearGrupo(nuevoGrupo)
        }
    }
}

class AgregarContactoViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarContactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarContactoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}