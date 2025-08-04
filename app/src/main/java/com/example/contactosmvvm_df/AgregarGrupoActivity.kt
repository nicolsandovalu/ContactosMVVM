package com.example.contactosmvvm_df

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.contactosmvvm_df.database.ContactosDatabase
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.repository.ContactosRepository
import com.example.contactosmvvm_df.viewmodel.ContactosViewModel
import com.example.contactosmvvm_df.viewmodel.ContactosViewModelFactory

class AgregarGrupoActivity : AppCompatActivity() {

    private val viewModel: ContactosViewModel by viewModels {
        val database = ContactosDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = ContactosRepository(
            database.contactoDao(),
            database.categoriaDao(),
            database.grupoDao()
        )
        ContactosViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_grupo)

        val etNombreGrupo = findViewById<EditText>(R.id.etNombreGrupo)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarGrupo)

        btnGuardar.setOnClickListener {
            val nombre = etNombreGrupo.text.toString()
            if (nombre.isNotBlank()) {
                viewModel.insertarGrupo(Grupo(nombre = nombre))
                Toast.makeText(this, "Grupo creado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Nombre vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}