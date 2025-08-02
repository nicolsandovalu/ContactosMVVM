package com.example.contactosmvvm_df.model

import androidx.room.Entity

@Entity(primaryKeys = ["contactoId", "grupoId"])
data class ContactoGrupoCrossRef(
    val contactoId: Int,
    val grupoId: Int
)