package com.example.contactosmvvm_df.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.contactosmvvm_df.model.Contacto


@Dao
interface ContactoDao {
    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerContactos(): LiveData<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE nombre LIKE :query")
    fun buscarContactos(query: String): LiveData<List<Contacto>>

    @Insert
    suspend fun insertar(contacto: Contacto): Long

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Delete
    suspend fun eliminar(contacto: Contacto)

    @Query("SELECT * FROM contactos WHERE id = :contactoId")
    fun obtenerContactoPorId(contactoId: Int): LiveData<Contacto>
}
