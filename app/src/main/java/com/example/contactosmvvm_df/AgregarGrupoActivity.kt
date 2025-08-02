package com.example.contactosmvvm_df

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.contactosmvvm_df.model.Grupo
import com.example.contactosmvvm_df.viewmodel.ContactosViewModel

class AgregarGrupoActivity : AppCompatActivity() {

    private lateinit var viewModel: ContactosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_grupo)

        viewModel = ViewModelProvider(this)[ContactosViewModel::class.java]

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
