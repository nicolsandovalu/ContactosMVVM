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

    // Se corrige la referencia a la propiedad 'grupos' del repositorio
    val todosLosGrupos: LiveData<List<Grupo>> = repository.grupos

    // Se corrige la referencia a la propiedad 'categorias' del repositorio
    val todasLasCategorias: LiveData<List<Categoria>> = repository.categorias

    var gruposDelContacto: LiveData<ContactoConGrupos> = MutableLiveData()

    /**
     * LiveData para observar el resultado de la operación de guardado.
     */
    private val _estadoGuardado = MutableLiveData<Result<Unit>>()

    /**
     * LiveData para observar el resultado de la operación de guardado.
     */
    val estadoGuardado: LiveData<Result<Unit>> = _estadoGuardado

    /**
     * Inserta un nuevo contacto en la base de datos y devuelve el ID.
     */
    suspend fun insertarContactoYObtenerId(contacto: Contacto): Long {
        return repository.insertarContacto(contacto)
    }

    /**
     * Actualiza un contacto existente en la base de datos.
     */
    fun actualizarContacto(contacto: Contacto) = viewModelScope.launch {
        try {
            repository.actualizarContacto(contacto)
            _estadoGuardado.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    /**
     * Obtiene un contacto por su ID. Y actualiza los grupos a los que pertenece el contacto.
     */
    fun getContactoById(contactoId: Int): LiveData<Contacto> {
        gruposDelContacto = repository.obtenerContactoConGrupos(contactoId)
        return repository.getContactoById(contactoId)
    }

    /** La función de guardado ahora necesita el ID del contacto guardado
     *y la lista de grupos a los que asociarlo.*/
    fun guardarContactoYAsociarGrupos(contacto: Contacto, grupoIds: List<Int>) = viewModelScope.launch {
        try {
            // Si el contacto es nuevo, insertamos y obtenemos su ID
            if (contacto.id == 0) {
                // Se usa la nueva función para obtener el ID
                val nuevoId = insertarContactoYObtenerId(contacto)
                // Se corrige el nombre de la función y se pasan los argumentos correctos
                repository.actualizarGruposDeContacto(nuevoId.toInt(), grupoIds)
                _estadoGuardado.postValue(Result.success(Unit))
            } else { // Si el contacto ya existe, actualizamos
                repository.actualizarContacto(contacto)
                // Se corrige el nombre de la función y se pasan los argumentos correctos
                repository.actualizarGruposDeContacto(contacto.id, grupoIds)
                _estadoGuardado.postValue(Result.success(Unit))
            }
        }catch(e: Exception) {
            _estadoGuardado.postValue(Result.failure(e))
        }
    }

    fun crearNuevoGrupo(nombreGrupo: String) = viewModelScope.launch {
        if (nombreGrupo.isNotBlank()) {
            val nuevoGrupo = Grupo(nombre = nombreGrupo)
            // Se corrige el nombre de la función del repositorio
            repository.insertarGrupo(nuevoGrupo)
        }
    }
}

/**
 * Fábrica para crear una instancia de AgregarContactoViewModel con dependencias.
 */
class AgregarContactoViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarContactoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgregarContactoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}