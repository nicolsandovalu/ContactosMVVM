package com.example.contactosmvvm_df

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ContactosViewModel
    private lateinit var contactosAdapter: ContactosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarViewModel()
        configurarToolbar()
        configurarRecyclerView()
        configurarFAB()
        observarDatos()
    }

    private fun configurarViewModel() {
        viewModel = ViewModelProvider(this)[ContactosViewModel::class.java]
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mis Contactos"
    }

    private fun configurarRecyclerView() {
        contactosAdapter = ContactosAdapter(emptyList()) { contacto ->
            // Manejar click en contacto
            mostrarOpcionesContacto(contacto)
        }

        binding.recyclerViewContactos.apply {
            adapter = contactosAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun configurarFAB() {
        binding.fabAgregarContacto.setOnClickListener {
            abrirAgregarContacto()
        }
    }

    private fun observarDatos() {
        // Observar lista de contactos
        viewModel.contactos.observe(this) { contactos ->
            contactos?.let {
                contactosAdapter.actualizarLista(it)

                // Mostrar mensaje si no hay contactos
                if (it.isEmpty()) {
                    binding.textViewNoContactos.visibility = android.view.View.VISIBLE
                    binding.recyclerViewContactos.visibility = android.view.View.GONE
                } else {
                    binding.textViewNoContactos.visibility = android.view.View.GONE
                    binding.recyclerViewContactos.visibility = android.view.View.VISIBLE
                }
            }
        }

        // Observar resultados de búsqueda si hay texto en el SearchView
        viewModel.contactosFiltrados.observe(this) { contactosFiltrados ->
            contactosFiltrados?.let {
                contactosAdapter.actualizarLista(it)
            }
        }
    }

    private fun mostrarOpcionesContacto(contacto: Contacto) {
        val opciones = arrayOf("Ver detalles", "Editar", "Eliminar", "Compartir")

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(contacto.nombre)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> verDetallesContacto(contacto)
                    1 -> editarContacto(contacto)
                    2 -> confirmarEliminarContacto(contacto)
                    3 -> compartirContacto(contacto)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verDetallesContacto(contacto: Contacto) {
        val mensaje = """
            Nombre: ${contacto.nombre}
            Teléfono: ${contacto.telefono}
            Email: ${contacto.email}
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Detalles del Contacto")
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun editarContacto(contacto: Contacto) {
        val intent = Intent(this, AgregarContactoActivity::class.java).apply {
            putExtra("CONTACTO_ID", contacto.id)
            putExtra("CONTACTO_NOMBRE", contacto.nombre)
            putExtra("CONTACTO_TELEFONO", contacto.telefono)
            putExtra("CONTACTO_EMAIL", contacto.email)
            putExtra("MODO_EDICION", true)
        }
        startActivity(intent)
    }

    private fun confirmarEliminarContacto(contacto: Contacto) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que quieres eliminar a ${contacto.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminar(contacto)
                Toast.makeText(this, "${contacto.nombre} eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun compartirContacto(contacto: Contacto) {
        val compartirTexto = """
            Contacto: ${contacto.nombre}
            Teléfono: ${contacto.telefono}
            Email: ${contacto.email}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, compartirTexto)
            putExtra(Intent.EXTRA_SUBJECT, "Contacto: ${contacto.nombre}")
        }

        startActivity(Intent.createChooser(intent, "Compartir contacto"))
    }

    private fun abrirAgregarContacto() {
        val intent = Intent(this, AgregarContactoActivity::class.java)
        startActivity(intent)
    }

    private fun abrirGestionarGrupos() {
        // TODO: Implementar gestión de grupos
        Toast.makeText(this, "Gestión de grupos - Por implementar", Toast.LENGTH_SHORT).show()
    }

    private fun exportarContactos() {
        viewModel.contactos.value?.let { contactos ->
            if (contactos.isNotEmpty()) {
                // Aquí usarías BackupUtils
                Toast.makeText(this, "Exportando ${contactos.size} contactos...", Toast.LENGTH_SHORT).show()
                // BackupUtils.exportar(this, contactos)
            } else {
                Toast.makeText(this, "No hay contactos para exportar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun importarContactos() {
        // TODO: Implementar importación
        Toast.makeText(this, "Importar contactos - Por implementar", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Configurar SearchView
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.buscar(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            R.id.action_grupos -> {
                abrirGestionarGrupos()
                true
            }
            R.id.action_exportar -> {
                exportarContactos()
                true
            }
            R.id.action_importar -> {
                importarContactos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}