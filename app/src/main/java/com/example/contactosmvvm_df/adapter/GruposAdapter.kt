package com.example.contactosmvvm_df.adapter

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.model.Contacto

class ContactosGrupoAdapter(
    private var contactos: List<Contacto> = emptyList(),
    private val onContactoClick: (Contacto) -> Unit = {},
    private val onRemoverContacto: (Contacto) -> Unit = {}
) : RecyclerView.Adapter<ContactosGrupoAdapter.ContactoGrupoViewHolder>() {

    inner class ContactoGrupoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val btnRemover: Button = itemView.findViewById(R.id.btnRemover)

        fun bind(contacto: Contacto) {
            tvNombre.text = contacto.nombre
            tvTelefono.text = contacto.telefono
            tvEmail.text = contacto.email

            // Click en toda la tarjeta
            itemView.setOnClickListener {
                onContactoClick(contacto)
            }

            // Botón para remover del grupo
            btnRemover.setOnClickListener {
                onRemoverContacto(contacto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoGrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto_grupo, parent, false)
        return ContactoGrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactoGrupoViewHolder, position: Int) {
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
        notifyItemInserted(contactos.size - 1)
    }
}