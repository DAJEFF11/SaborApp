package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.model.DetallePedido
import com.senati.saborapk.databinding.ItemDetallePedidoBinding

class DetallePedidoAdapter :
    ListAdapter<DetallePedido, DetallePedidoAdapter.DetalleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetalleViewHolder {
        val binding = ItemDetallePedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetalleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetalleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetalleViewHolder(
        private val binding: ItemDetallePedidoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(detalle: DetallePedido) = with(binding) {
            tvCantidadNombre.text = root.context.getString(
                R.string.cantidad_nombre_plato,
                detalle.cantidad,
                detalle.nombrePlato
            )
            tvSubtotal.text = root.context.getString(R.string.moneda, detalle.subtotal)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<DetallePedido>() {
        override fun areItemsTheSame(oldItem: DetallePedido, newItem: DetallePedido) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DetallePedido, newItem: DetallePedido) =
            oldItem == newItem
    }
}
