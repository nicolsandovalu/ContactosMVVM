package com.example.contactosmvvm_df.utils

import android.util.Patterns

object ValidationUtils {
    fun isNombreValido(nombre: String): Boolean {
        return nombre.isNotBlank()
    }

    fun isTelefonoValido(telefono: String): Boolean {
        return telefono.isNotBlank()
    }

    fun isEmailValido(email: String): Boolean {
        return email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}