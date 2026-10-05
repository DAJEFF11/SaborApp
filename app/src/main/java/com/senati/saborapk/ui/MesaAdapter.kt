package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.model.Mesa
import com.senati.saborapk.databinding.ItemMesaBinding

class MesaAdapter : ListAdapter<Mesa, MesaAdapter.MesaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MesaViewHolder(
        private val binding: ItemMesaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mesa: Mesa) = with(binding) {
            tvNumeroMesa.text = root.context.getString(R.string.numero_mesa, mesa.numero)
            tvCapacidadMesa.text = root.resources.getQuantityString(
                R.plurals.capacidad_mesa,
                mesa.capacidad,
                mesa.capacidad
            )
            tvEstadoMesa.text = mesa.estado
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Mesa>() {
        override fun areItemsTheSame(oldItem: Mesa, newItem: Mesa) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Mesa, newItem: Mesa) = oldItem == newItem
    }
}
