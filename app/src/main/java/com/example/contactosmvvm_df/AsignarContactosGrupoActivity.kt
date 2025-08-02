package com.example.contactosmvvm_df

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactosmvvm_df.adapter.ContactosSeleccionAdapter
import com.example.contactosmvvm_df.databinding.ActivityAsignarContactosBinding
import com.example.contactosmvvm_df.viewmodel.ContactosViewModel

class AsignarContactosGrupoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAsignarContactosBinding
    private lateinit var viewModel: ContactosViewModel
    private lateinit var adapter: ContactosSeleccionAdapter
    private var grupoId: Int = -1
    private var grupoNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsignarContactosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarDatos()
        if (grupoId == -1) {
            mostrarErrorYCerrar()
            return
        }

        configurarViewModel()
        configurarInterfaz()
        configurarRecyclerView()
        observarDatos()
    }

    private fun inicializarDatos() {
        grupoId = intent.getIntExtra("GRUPO_ID", -1)
        grupoNombre = intent.getStringExtra("GRUPO_NOMBRE") ?: "Grupo"
    }

    private fun mostrarErrorYCerrar() {
        Toast.makeText(this, "Error: ID de grupo inválido", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun configurarViewModel() {
        viewModel = ViewModelProvider(this)[ContactosViewModel::class.java]
    }

    private fun configurarInterfaz() {
        binding.tvTitulo.text = "Asignar contactos a: $grupoNombre"

        binding.btnGuardar.setOnClickListener {
            procesarGuardado()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }

        supportActionBar?.apply {
            title = "Asignar Contactos"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun configurarRecyclerView() {
        adapter = ContactosSeleccionAdapter { contacto, seleccionado ->
            // Por ahora no hacemos nada especial aquí
            // El adapter maneja el estado internamente
        }

        binding.recyclerViewContactos.apply {
            layoutManager = LinearLayoutManager(this@AsignarContactosGrupoActivity)
            adapter = this@AsignarContactosGrupoActivity.adapter
        }
    }

    private fun observarDatos() {
        // Observar lista de contactos
        viewModel.contactos.observe(this) { listaContactos ->
            listaContactos?.let {
                adapter.actualizarLista(it)
            }
        }

        // Observar contactos que ya están en el grupo
        viewModel.obtenerContactosDeGrupo(grupoId).observe(this) { grupoConContactos ->
            grupoConContactos?.let { grupo ->
                val idsEnGrupo = grupo.contactos.map { it.id }.toSet()
                adapter.marcarContactosExistentes(idsEnGrupo)
            }
        }
    }

    private fun procesarGuardado() {
        try {
            val nuevosContactos = adapter.obtenerContactosSeleccionados()
            val contactosARemover = adapter.obtenerContactosDeseleccionados()

            // Agregar nuevos contactos al grupo
            nuevosContactos.forEach { contacto ->
                viewModel.asociarContactoAGrupo(contacto.id, grupoId)
            }

            // Remover contactos del grupo
            contactosARemover.forEach { contacto ->
                viewModel.removerContactoDeGrupo(contacto.id, grupoId)
            }

            // Mostrar mensaje de confirmación
            val totalCambios = nuevosContactos.size + contactosARemover.size
            val mensaje = when {
                totalCambios == 0 -> "No hay cambios para guardar"
                else -> "Guardado: ${nuevosContactos.size} agregados, ${contactosARemover.size} removidos"
            }

            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}