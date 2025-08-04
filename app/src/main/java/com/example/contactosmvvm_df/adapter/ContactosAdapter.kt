package com.example.contactosmvvm_df.adapter

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactosmvvm_df.databinding.ItemContactoBinding
import com.example.contactosmvvm_df.model.Contacto



/** interfaz para manejar los eventos de clic en los botones de llamada y mensaje.*/
interface OnContactActionListener {
    fun onCallClick(telefono: String)
    fun onMessageClick(telefono: String)
    fun onItemClick(contacto: Contacto)
}

class ContactosAdapter(

    private val listener: OnContactActionListener) :
        ListAdapter<Contacto, ContactosAdapter.ContactoViewHolder>(DiffCallback) {

            //ViewHolder que contiene la vista de un solo item de contacto.
    inner class ContactoViewHolder(private var binding: ItemContactoBinding) :
            RecyclerView.ViewHolder(binding.root){

                //Vincula los datos del contacto a las vistas del layout.

        fun bind(contacto: Contacto, actionListener: OnContactActionListener) {
            binding.contacto = contacto

            binding.executePendingBindings()

            binding.ivCall.setOnClickListener {
                actionListener.onCallClick(contacto.telefono)
            }

            binding.ivMessage.setOnClickListener {
                actionListener.onMessageClick(contacto.telefono)
             }

            itemView.setOnClickListener {
                actionListener.onItemClick(contacto)
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Contacto>() {

        override fun areItemsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contacto, newItem: Contacto): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = ItemContactoBinding.inflate(layoutInflater, parent, false)
        return ContactoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        val contacto = getItem(position)
        holder.bind(contacto, listener)
    }
}

