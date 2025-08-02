package com.example.contactosmvvm_df

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.viewmodel.ContactosViewModel
import com.example.contactosmvvm_df.databinding.ActivityAgregarContactoBinding

class AgregarContactoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarContactoBinding
    private lateinit var viewModel: ContactosViewModel

    private var modoEdicion = false
    private var contactoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ContactosViewModel::class.java]

        obtenerDatosIntent()
        setupUI()
    }

    private fun obtenerDatosIntent() {
        modoEdicion = intent.getBooleanExtra("MODO_EDICION", false)

        if (modoEdicion) {
            contactoId = intent.getIntExtra("CONTACTO_ID", 0)
            val nombre = intent.getStringExtra("CONTACTO_NOMBRE") ?: ""
            val telefono = intent.getStringExtra("CONTACTO_TELEFONO") ?: ""
            val email = intent.getStringExtra("CONTACTO_EMAIL") ?: ""

            // Llenar campos con datos existentes
            binding.etNombre.setText(nombre)
            binding.etTelefono.setText(telefono)
            binding.etEmail.setText(email)
        }
    }

    private fun setupUI() {
        // Cambiar título según el modo
        val titulo = if (modoEdicion) "Editar Contacto" else "Agregar Contacto"
        binding.tvTitulo.text = titulo

        // Cambiar texto del botón
        val textoBoton = if (modoEdicion) "Actualizar" else "Guardar"
        binding.btnGuardar.text = textoBoton

        // Configurar toolbar
        supportActionBar?.apply {
            title = titulo
            setDisplayHomeAsUpEnabled(true)
        }

        // Configurar listeners
        binding.btnGuardar.setOnClickListener {
            if (modoEdicion) {
                actualizarContacto()
            } else {
                guardarContacto()
            }
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun guardarContacto() {
        val nombre = binding.etNombre.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (validarFormulario(nombre, telefono, email)) {
            val contacto = Contacto(
                nombre = nombre,
                telefono = telefono,
                email = email,
                categoriaId = 1 // Categoría por defecto
            )

            viewModel.insertar(contacto)
            finish()
        }
    }

    private fun actualizarContacto() {
        val nombre = binding.etNombre.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (validarFormulario(nombre, telefono, email)) {
            val contacto = Contacto(
                id = contactoId,
                nombre = nombre,
                telefono = telefono,
                email = email,
                categoriaId = 1 // Mantener categoría por defecto
            )

            viewModel.actualizar(contacto)
            finish()
        }
    }

    private fun validarFormulario(nombre: String, telefono: String, email: String): Boolean {
        var esValido = true

        // Limpiar errores previos
        binding.tilNombre.error = null
        binding.tilTelefono.error = null
        binding.tilEmail.error = null

        // Validar nombre
        if (nombre.isBlank()) {
            binding.tilNombre.error = "El nombre es requerido"
            esValido = false
        }

        // Validar teléfono
        if (!telefono.matches(Regex("^\\+?[0-9]{7,15}$"))) {
            binding.tilTelefono.error = "Formato de teléfono inválido"
            esValido = false
        }

        // Validar email
        if (!email.matches(Regex("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))) {
            binding.tilEmail.error = "Formato de email inválido"
            esValido = false
        }

        return esValido
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}