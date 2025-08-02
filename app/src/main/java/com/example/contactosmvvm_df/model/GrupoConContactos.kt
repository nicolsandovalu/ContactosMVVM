package com.example.contactosmvvm_df.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GrupoConContactos(
    @Embedded val grupo: Grupo,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            ContactoGrupoCrossRef::class,
            parentColumn = "grupoId",     // Columna en CrossRef que apunta al Grupo
            entityColumn = "contactoId"   // Columna en CrossRef que apunta al Contacto
        )
    )
    val contactos: List<Contacto>
)