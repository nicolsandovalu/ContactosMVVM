package com.example.contactosmvvm_df.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ContactoConGrupos(
    @Embedded val contacto: Contacto,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            ContactoGrupoCrossRef::class,
            parentColumn = "contactoId",  // Columna en CrossRef que apunta al Contacto
            entityColumn = "grupoId"      // Columna en CrossRef que apunta al Grupo
        )
    )
    val grupos: List<Grupo>
)