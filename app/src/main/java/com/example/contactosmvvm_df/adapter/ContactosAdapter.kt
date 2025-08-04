package com.example.contactosmvvm_df.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.databinding.ItemContactoBinding
import com.example.contactosmvvm_df.model.Contacto


class ContactosAdapter(
    private val onItemClick: (Contacto) -> Unit,
    private val onCallClick: (String) -> Unit,
    private val onMessageClick: (String) -> Unit
) : ListAdapter<Contacto, ContactosAdapter.ContactoViewHolder>(DiffCallback) {


    inner class ContactoViewHolder(private var binding: ItemContactoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos del contacto a las vistas del layout.
         */
        fun bind(contacto: Contacto) {
            // Usa el data binding para asignar el objeto contacto a la variable del layout
            binding.contacto = contacto
            binding.executePendingBindings()

            // Configurar listeners para los botones y el item completo
            binding.ivCall.setOnClickListener {
                onCallClick(contacto.telefono)
            }

            binding.ivMessage.setOnClickListener {
                onMessageClick(contacto.telefono)
            }

            itemView.setOnClickListener {
                onItemClick(contacto)
            }
        }
    }

    /**
     * Callback para calcular las diferencias entre dos listas de contactos.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Contacto>() {
        /**
         * Comprueba si los items son los mismos (por su ID).
         */
        override fun areItemsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem.id == newItem.id
        }

        /**
         * Comprueba si el contenido de los items ha cambiado.
         */
        override fun areContentsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Crea nuevos ViewHolders cuando el RecyclerView lo necesita.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemContactoBinding.inflate(layoutInflater, parent, false)
        return ContactoViewHolder(binding)
    }

    /**
     * Vincula los datos de un contacto a un ViewHolder en una posición específica.
     */
    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = getItem(position)
        holder.bind(contacto)
    }
}