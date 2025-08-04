package com.example.contactosmvvm_df

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.contactosmvvm_df.database.ContactosDatabase
import com.example.contactosmvvm_df.databinding.ActivityAgregarContactoBinding
import com.example.contactosmvvm_df.model.Categoria
import com.example.contactosmvvm_df.model.Contacto
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.repository.ContactosRepository
import com.example.contactosmvvm_df.utils.ValidationUtils
import com.example.contactosmvvm_df.viewmodel.AgregarContactoViewModel
import com.example.contactosmvvm_df.viewmodel.AgregarContactoViewModelFactory

class AgregarContactoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarContactoBinding
    private var contactoId: Int? = null
    private var contactoExistente: Contacto? = null
    private var listaCategorias = listOf<Categoria>()
    private var categoriaSeleccionadaId: Int? = null
    private var listaTodosLosGrupos = listOf<Grupo>()
    private var idsGruposSeleccionados = mutableSetOf<Int>()
    private val viewModel: AgregarContactoViewModel by viewModels {
        val database = ContactosDatabase.getDatabase(
            context = applicationContext,
            coroutineScope = lifecycleScope
        )
        val repository = ContactosRepository(
            database.contactoDao(),
            database.categoriaDao(),
            database.grupoDao()
        )
        AgregarContactoViewModelFactory(repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarContactoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactoId = intent.getIntExtra("EXTRA_CONTACTO_ID", -1).takeIf { it != -1 }
        setupViews()
        setupListeners()
        observeViewModel()
        setupSpinner()
        observarCategorias()
        setupGroupSelection()
    }
    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        if (contactoId != null) {
            title = "Editar Contacto"
            binding.textViewTitulo.text = title
            binding.buttonGuardar.text = "Actualizar contacto"
            viewModel.getContactoById(contactoId!!).observe(this) { contacto ->
                contactoExistente = contacto
                if (contacto != null) {
                    binding.editTextNombre.setText(contacto.nombre)
                    binding.editTextTelefono.setText(contacto.telefono)
                    binding.editTextEmail.setText(contacto.email)

                } else {
                    Toast.makeText(this, "Contacto no encontrado", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } else {
            title = "Agregar Contacto"
            binding.textViewTitulo.text = title
        }
    }
    private fun setupListeners() {
        binding.buttonGuardar.setOnClickListener {
            guardarContacto()
        }
    }
    private fun observeViewModel() {
        viewModel.estadoGuardado.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Contacto guardado", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun guardarContacto() {
        val nombre = binding.editTextNombre.text.toString().trim()
        val telefono = binding.editTextTelefono.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val categoriaId = categoriaSeleccionadaId

        binding.textFieldNombre.error = null
        binding.textFieldTelefono.error = null
        binding.textFieldEmail.error = null
        if (!ValidationUtils.isNombreValido(nombre)) {
            binding.textFieldNombre.error = "El nombre es obligatorio"
            return
        }
        if (!ValidationUtils.isTelefonoValido(telefono)) {
            binding.textFieldTelefono.error = "El teléfono es obligatorio"
            return
        }
        if (!ValidationUtils.isEmailValido(email)) {
            binding.textFieldEmail.error = "Email no válido"
            return
        }
        val contacto = Contacto(
            id = contactoId ?: 0,
            nombre = nombre,
            telefono = telefono,
            email = email,
            categoriaId = categoriaId

        )
        viewModel.guardarContactoYAsociarGrupos(contacto, idsGruposSeleccionados.toList())
    }
    private fun observarCategorias() {
        viewModel.todasLasCategorias.observe(this) { categorias ->
            categorias?.let {
                listaCategorias = it
                val nombresCategorias = it.map { cat -> cat.nombre }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresCategorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategoria.adapter = adapter
                contactoExistente?.let { contacto ->
                    val categoriaDelContacto =
                        listaCategorias.find { c -> c.id == contacto.categoriaId }
                    val posicion = listaCategorias.indexOf(categoriaDelContacto)
                    if (posicion != -1) {
                        binding.spinnerCategoria.setSelection(posicion)
                    }
                }
            }
        }
    }
    private fun setupSpinner() {
        binding.spinnerCategoria.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (listaCategorias.isNotEmpty()) {
                        categoriaSeleccionadaId = listaCategorias[position].id
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }
    private fun setupGroupSelection() {
        viewModel.todosLosGrupos.observe(this) { grupos ->
            listaTodosLosGrupos = grupos
        }
        viewModel.gruposDelContacto.observe(this) { contactoConGrupos ->
            if (contactoConGrupos != null) {
                idsGruposSeleccionados = contactoConGrupos.grupos.map { it.id }.toMutableSet()
                actualizarTextoGruposSeleccionados()
            }
        }
        binding.btnSeleccionarGrupos.setOnClickListener {
            mostrarDialogoSeleccionGrupos()
        }
    }
    private fun mostrarDialogoSeleccionGrupos() {
        val nombresGrupos = listaTodosLosGrupos.map { it.nombre }.toTypedArray()
        val checkedItems = listaTodosLosGrupos.map { idsGruposSeleccionados.contains(it.id) }.toBooleanArray()
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Grupos")
            .setMultiChoiceItems(nombresGrupos, checkedItems) { _, which, isChecked ->
                val id = listaTodosLosGrupos[which].id
                if (isChecked) {
                    idsGruposSeleccionados.add(id)
                } else {
                    idsGruposSeleccionados.remove(id)
                }
            }
            .setPositiveButton("Aceptar") { dialog, _ ->
                actualizarTextoGruposSeleccionados()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Crear Grupo") { _, _ ->
                mostrarDialogoCrearGrupo()
            }
            .show()
    }
    private fun mostrarDialogoCrearGrupo() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear Nuevo Grupo")
        val input = EditText(this)
        input.hint = "Nombre del grupo"
        builder.setView(input)
        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombreGrupo = input.text.toString().trim()
            if (nombreGrupo.isNotEmpty()) {
                viewModel.crearNuevoGrupo(nombreGrupo)
                Toast.makeText(this, "Grupo '$nombreGrupo' creado.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
    private fun actualizarTextoGruposSeleccionados() {
        val nombresSeleccionados = listaTodosLosGrupos
            .filter { idsGruposSeleccionados.contains(it.id) }
            .joinToString(separator = ", ") { it.nombre }
        binding.tvGruposSeleccionados.text = if (nombresSeleccionados.isEmpty()) "Ninguno" else nombresSeleccionados
    }
    companion object {
        const val EXTRA_CONTACTO_ID = "EXTRA_CONTACTO_ID"
    }
}