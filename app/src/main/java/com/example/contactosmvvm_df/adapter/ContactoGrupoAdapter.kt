package com.example.contactosmvvm_df.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.model.Contacto

class ContactoGrupoAdapter(

    private var contactos: List<Contacto> = emptyList(),
    private val onContactoClick: (Contacto) -> Unit = {},
    private val onRemoverContacto: (Contacto) -> Unit = {}
) : RecyclerView.Adapter<ContactosGrupoAdapter.ContactoGrupoViewHolder>() {

    inner class ContactoGrupoViewHolder(
        private val binding: ItemContactoGrupoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contacto: Contacto) {
            binding.apply {
                tvNombre.text = contacto.nombre
                tvTelefono.text = contacto.telefono
                tvEmail.text = contacto.email

                root.setOnClickListener {
                    onContactoClick(Contacto)
                }

                btnRemover.setOnClickListener {
                    onRemoverContacto(Contacto)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactoGrupoViewHolder {

        val binding = ItemContactoGrupoBinding.inflate (
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ContactoGrupoViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: ContactoGrupoViewHolder,
        position: Int) {

        holder.bind(contactos[position])
    }

    override fun getItemCount(): Int = contactos.size

    fun actualizarContactos(nuevosContactos: List<Contacto>) {
        contactos = nuevosContactos
        notifyDataSetChanged()
    }

    fun removerContacto(contacto: Contacto) {
        val posicion = contactos.indexOf(contacto)
        if (posicion != -1) {
            val nuevaLista = contactos.toMutableList()
            nuevaLista.removeAt(posicion)
            contactos = nuevaLista
            notifyItemRemoved(posicion)
        }
    }

    fun agregarContacto(contacto: Contacto) {
        val nuevaLista = contactos.toMutableList()
        nuevaLista.add(contacto)
        contactos = nuevaLista
        notifyItemInserted(contactos.size -1)
    }
}