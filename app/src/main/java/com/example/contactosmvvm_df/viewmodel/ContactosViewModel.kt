package com.example.contactosmvvm_df.viewmodel
import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.example.contactosmvvm_df.database.ContactosDatabase
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.ContactoConGrupos
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.model.GrupoConContactos
import com.example.contactosmvvm_df.repository.ContactosRepository
import kotlinx.coroutines.launch
import kotlin.math.E

/**Enum para representar los estados de la importacion*/
enum class EstadoImportacion {VACIO, CARGANDO, EXITO, ERROR}


//ViewModel para gestionar la lista principal de contactos.
class ContactosViewModel(private val repository: ContactosRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String> ("")

    private val _estadoImportacion = MutableLiveData<EstadoImportacion>(EstadoImportacion.VACIO)
    //LiveData para comunicar el estado de la importación a la UI
    val estadoImportacion: LiveData<EstadoImportacion> get() = _estadoImportacion

    //LiveData que expone la lista de contactos, se actualiza según la búsqueda
    val contactos: LiveData<List<Contacto>> = _searchQuery.switchMap { query ->
        if (query.isNullOrEmpty()) {
            repository.contactos
        } else {
            repository.buscarContactos(query)
        }
    }

    //Inicia búsqueda de contactos
    fun buscarContacto(query: String) {
        _searchQuery.value = query
    }

    //elimina un contacto de la base de datos

    fun eliminarContacto(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }

    //Devuelve el valor actual de la lista de contactos

    fun getContactosParaExportar(): List<Contacto>? {
        return contactos.value
    }

    //inicia proceso de importación de contactos desde el dispositivo
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

    //FUNCION PARA RESETEAR EL ESTADO DE IMPORTACIÓN
    fun resetearEstadoImportacion() {
        _estadoImportacion.value = EstadoImportacion.VACIO
    }
}

//Crear una instancia de ContactosVM con dependencias

class ContactosViewModelFactory(private val repository: ContactosRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(ContactosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}