package com.example.contactosmvvm_df.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "contactos",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["categoria_id"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val email: String?,

    @ColumnInfo(name = "categoria_id", index = true)
    val categoriaId: Int?
)