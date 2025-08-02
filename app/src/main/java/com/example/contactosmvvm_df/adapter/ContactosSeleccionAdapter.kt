package com.example.contactosmvvm_df.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.databinding.ItemCheckboxContactoBinding
import com.example.contactosmvvm_df.model.Contacto


data class ContactosSeleccionEstado(

    val contacto: Contacto,
    var isSelected: Boolean = false,
    var estaEnGrupo: Boolean = false

)

class ContactosSeleccionAdapter(

    private val onSelectionChanged: (Contacto, Boolean) -> Unit
) : RecyclerView.Adapter<ContactosSeleccionAdapter.ContactoSeleccionViewHolder> () {

    private var estadosSeleccion: MutableList<ContactosSeleccionEstado> = mutableListOf()

    inner class ContactoSeleccionViewHolder(
        private val binding: ItemCheckboxContactoBinding

    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(estado: ContactosSeleccionEstado) {
            with(binding) {
                // Configurar datos del contacto
                tvNombre.text = estado.contacto.nombre
                tvTelefono.text = estado.contacto.telefono
                tvEmail.text = estado.contacto.email ?: ""

                // Configurar estado del checkbox
                checkbox.isChecked = estado.isSelected || estado.estaEnGrupo
                checkbox.isEnabled = !estado.estaEnGrupo

                // Estilo para contactos ya en grupo
                if (estado.estaEnGrupo) {
                    tvNombre.text = "${estado.contacto.nombre} (Ya en grupo)"
                    root.alpha = 0.7f
                } else {
                    tvNombre.text = estado.contacto.nombre
                    root.alpha = 1.0f
                }

                // Listeners
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (!estado.estaEnGrupo) {
                        estado.isSelected = isChecked
                        onSelectionChanged(estado.contacto, isChecked)
                    }
                }

                root.setOnClickListener {
                    if (!estado.estaEnGrupo) {
                        val newState = !checkbox.isChecked
                        checkbox.isChecked = newState
                        estado.isSelected = newState
                        onSelectionChanged(estado.contacto, newState)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoSeleccionViewHolder {
        val binding = ItemCheckboxContactoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactoSeleccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactoSeleccionViewHolder, position: Int) {
        holder.bind(estadosSeleccion[position])
    }

    override fun getItemCount(): Int = estadosSeleccion.size

    // Métodos públicos
    fun actualizarLista(contactos: List<Contacto>) {
        val nuevosEstados = contactos.map { contacto ->
            estadosSeleccion.find { it.contacto.id == contacto.id }?.let {
                it.copy(contacto = contacto)
            } ?: ContactosSeleccionEstado(contacto)
        }
        estadosSeleccion.clear()
        estadosSeleccion.addAll(nuevosEstados)
        notifyDataSetChanged()
    }

    fun marcarContactosExistentes(idsContactos: Set<Int>) {
        estadosSeleccion.forEach { estado ->
            estado.estaEnGrupo = idsContactos.contains(estado.contacto.id)
        }
        notifyDataSetChanged()
    }

    fun obtenerContactosSeleccionados(): List<Contacto> = estadosSeleccion
        .filter { it.isSelected && !it.estaEnGrupo }
        .map { it.contacto }

    fun obtenerContactosDeseleccionados(): List<Contacto> = estadosSeleccion
        .filter { !it.isSelected && it.estaEnGrupo }
        .map { it.contacto }

    fun obtenerTodosLosSeleccionados(): List<Contacto> = estadosSeleccion
        .filter { it.isSelected || it.estaEnGrupo }
        .map { it.contacto }

    fun limpiarSelecciones() {
        estadosSeleccion.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun seleccionarTodos() {
        estadosSeleccion.forEach { if (!it.estaEnGrupo) it.isSelected = true }
        notifyDataSetChanged()
    }
}