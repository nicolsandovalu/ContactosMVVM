package com.example.contactosmvvm_df.model

import androidx.room.Entity
import androidx.room.Index

@Entity(primaryKeys = ["contactoId", "grupoId"],
    indices = [
        Index(value = ["contactoId"]),
        Index(value = ["grupoId"])
    ]
)

data class ContactoGrupoCrossRef(
    val contactoId: Int,
    val grupoId: Int
)