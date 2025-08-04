package com.example.contactosmvvm_df.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.contactosmvvm_df.model.Categoria

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerCategorias(): LiveData<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(categoria: Categoria)
}
