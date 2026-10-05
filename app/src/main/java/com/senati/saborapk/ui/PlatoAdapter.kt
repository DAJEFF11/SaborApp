package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.model.Plato
import com.senati.saborapk.databinding.ItemPlatoBinding

class PlatoAdapter(
    private val onPlatoClick: (Plato) -> Unit
) : ListAdapter<Plato, PlatoAdapter.PlatoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatoViewHolder {
        val binding = ItemPlatoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlatoViewHolder(binding, onPlatoClick)
    }

    override fun onBindViewHolder(holder: PlatoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlatoViewHolder(
        private val binding: ItemPlatoBinding,
        private val onPlatoClick: (Plato) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plato: Plato) = with(binding) {
            tvNombrePlato.text = plato.nombre
            tvDetallePlato.text = root.context.getString(
                R.string.detalle_plato,
                plato.categoria,
                plato.precio
            )
            tvDisponibilidad.text = root.context.getString(
                if (plato.disponible) R.string.disponible else R.string.agotado
            )
            val textColor = if (plato.disponible) R.color.text_secondary else R.color.accent_red
            tvDisponibilidad.setTextColor(ContextCompat.getColor(root.context, textColor))
            tvDisponibilidad.setBackgroundResource(
                if (plato.disponible) R.drawable.bg_badge_success else R.drawable.bg_badge_danger
            )
            root.setOnClickListener { onPlatoClick(plato) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Plato>() {
        override fun areItemsTheSame(oldItem: Plato, newItem: Plato) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Plato, newItem: Plato) = oldItem == newItem
    }
}
