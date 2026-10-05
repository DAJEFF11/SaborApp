package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.model.VentaPorMesa
import com.senati.saborapk.databinding.ItemVentaMesaBinding

class VentaMesaAdapter : ListAdapter<VentaPorMesa, VentaMesaAdapter.VentaMesaViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaMesaViewHolder {
        val binding = ItemVentaMesaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VentaMesaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VentaMesaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VentaMesaViewHolder(
        private val binding: ItemVentaMesaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VentaPorMesa) = with(binding) {
            tvMesaReporte.text = root.context.getString(R.string.numero_mesa, item.numeroMesa)
            tvVentaMesa.text = root.context.getString(R.string.moneda, item.total)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<VentaPorMesa>() {
        override fun areItemsTheSame(oldItem: VentaPorMesa, newItem: VentaPorMesa) =
            oldItem.numeroMesa == newItem.numeroMesa
        override fun areContentsTheSame(oldItem: VentaPorMesa, newItem: VentaPorMesa) = oldItem == newItem
    }
}
