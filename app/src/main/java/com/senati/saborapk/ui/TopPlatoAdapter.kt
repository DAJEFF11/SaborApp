package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.model.TopPlato
import com.senati.saborapk.databinding.ItemTopPlatoBinding

class TopPlatoAdapter : ListAdapter<TopPlato, TopPlatoAdapter.TopPlatoViewHolder>(DiffCallback) {
    private var maxCantidad = 1

    fun mostrar(items: List<TopPlato>) {
        maxCantidad = items.maxOfOrNull { it.cantidad }?.coerceAtLeast(1) ?: 1
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopPlatoViewHolder {
        val binding = ItemTopPlatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopPlatoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopPlatoViewHolder, position: Int) {
        holder.bind(getItem(position), maxCantidad)
    }

    class TopPlatoViewHolder(
        private val binding: ItemTopPlatoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopPlato, maxCantidad: Int) = with(binding) {
            tvNombreTop.text = item.nombre
            tvCantidadTop.text = root.context.getString(R.string.cantidad_vendida, item.cantidad)
            progressTop.max = maxCantidad
            progressTop.progress = item.cantidad
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TopPlato>() {
        override fun areItemsTheSame(oldItem: TopPlato, newItem: TopPlato) = oldItem.nombre == newItem.nombre
        override fun areContentsTheSame(oldItem: TopPlato, newItem: TopPlato) = oldItem == newItem
    }
}
