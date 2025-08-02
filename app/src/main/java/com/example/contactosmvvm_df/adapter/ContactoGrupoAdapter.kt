package com.example.contactosmvvm_df.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.databinding.ItemContactoGrupoBinding
import com.example.contactosmvvm_df.model.Contacto

class ContactoGrupoAdapter (

    private val onContactoClick: (Contacto) -> Unit,
    private val onRemoverContacto: (Contacto) -> Unit
): ListAdapter<Contacto, ContactoGrupoAdapter.ContactoGrupoViewHolder>(ContactoDiffCallback()) {

    inner class ContactoGrupoViewHolder(private val binding: ItemContactoGrupoBinding) :
        RecyclerView.ViewHolder(binding.root){

            fun bind(contacto: Contacto) {
                binding.apply {
                    tvNombre.text = contacto.nombre
                    tvTelefono.text = contacto.telefono
                    tvEmail.text = contacto.email

                    root.setOnClickListener {
                        onContactoClick (contacto) //
                    }

                    btnRemover.setOnClickListener {
                        onRemoverContacto(contacto)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoGrupoViewHolder {
        val binding = ItemContactoGrupoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactoGrupoViewHolder(binding)
    }

    //Enlaza datos en un ViewHolder existente
    override fun onBindViewHolder(holder: ContactoGrupoViewHolder, position: Int) {
        val contacto = getItem(position)
        holder.bind(contacto)
    }

    /**
     * Objeto de utilidad para calcular las diferencias entre dos listas de contactos.
     * Esta clase es esencial para el funcionamiento de ListAdapter.
     */

    private class ContactoDiffCallback: DiffUtil.ItemCallback<Contacto>() {
        override fun areItemsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem == newItem
        }
    }


}