package com.example.contactosmvvm_df.database
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.contactosmvvm_df.model.ContactoConGrupos
import com.example.contactosmvvm_df.model.ContactoGrupoCrossRef
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.model.GrupoConContactos
@Dao
interface GrupoDao {
    @Insert
    suspend fun insertarGrupo(grupo: Grupo)

    @Update
    suspend fun actualizarGrupo(grupo: Grupo)

    @Delete
    suspend fun eliminarGrupo(grupo: Grupo)

    @Query("SELECT * FROM grupos ORDER BY nombre ASC")
    fun obtenerGrupos(): LiveData<List<Grupo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarContactoGrupoCrossRef(ref: ContactoGrupoCrossRef)

    @Delete
    suspend fun eliminarContactoGrupoCrossRef(ref: ContactoGrupoCrossRef)

    @Query("DELETE FROM ContactoGrupoCrossRef WHERE contactoId = :contactoId")
    suspend fun eliminarTodasLasReferenciasDeContacto(contactoId: Int)

    @Transaction
    @Query("SELECT * FROM grupos WHERE id = :grupoId")
    fun obtenerGrupoConContactos(grupoId: Int): LiveData<GrupoConContactos>

    @Transaction
    @Query("SELECT * FROM grupos ORDER BY nombre ASC")
    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>>

    @Transaction
    @Query("SELECT * FROM contactos WHERE id = :contactoId")
    fun obtenerContactoConGrupos(contactoId: Int): LiveData<ContactoConGrupos>

    @Query("SELECT COUNT(*) FROM contactos c INNER JOIN ContactoGrupoCrossRef cgr ON c.id = cgr.contactoId WHERE cgr.grupoId = :grupoId")
    suspend fun contarContactosEnGrupo(grupoId: Int): Int
}