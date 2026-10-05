package com.senati.saborapk.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senati.saborapk.R
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.model.Mesa
import com.senati.saborapk.databinding.ItemMesaPedidoBinding

class MesaPedidoAdapter(
    private val onMesaClick: (Mesa) -> Unit
) : ListAdapter<Mesa, MesaPedidoAdapter.MesaViewHolder>(DiffCallback) {

    var mesaSeleccionadaId: Long? = null
        set(value) {
            val anterior = currentList.indexOfFirst { it.id == field }
            field = value
            val nueva = currentList.indexOfFirst { it.id == value }
            if (anterior >= 0) notifyItemChanged(anterior)
            if (nueva >= 0 && nueva != anterior) notifyItemChanged(nueva)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesaViewHolder {
        val binding = ItemMesaPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MesaViewHolder(binding, onMesaClick)
    }

    override fun onBindViewHolder(holder: MesaViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).id == mesaSeleccionadaId)
    }

    class MesaViewHolder(
        private val binding: ItemMesaPedidoBinding,
        private val onMesaClick: (Mesa) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mesa: Mesa, seleccionada: Boolean) = with(binding) {
            tvNumeroMesa.text = root.context.getString(R.string.numero_mesa, mesa.numero)
            tvEstadoMesa.text = root.context.getString(
                if (mesa.estado == MesaDao.ESTADO_OCUPADA) R.string.ocupada else R.string.libre
            )
            val ocupada = mesa.estado == MesaDao.ESTADO_OCUPADA
            tvEstadoMesa.setTextColor(
                ContextCompat.getColor(root.context, if (ocupada) R.color.accent_red else R.color.success)
            )
            tvEstadoMesa.setBackgroundResource(
                if (ocupada) R.drawable.bg_badge_danger else R.drawable.bg_badge_success
            )
            val fondo = if (mesa.estado == MesaDao.ESTADO_OCUPADA) {
                R.color.primary_light
            } else {
                R.color.white
            }
            cardMesa.setCardBackgroundColor(ContextCompat.getColor(root.context, fondo))
            cardMesa.strokeColor = ContextCompat.getColor(
                root.context,
                if (seleccionada) R.color.primary else R.color.outline
            )
            cardMesa.strokeWidth = root.resources.getDimensionPixelSize(
                if (seleccionada) R.dimen.stroke_selected else R.dimen.stroke_normal
            )
            root.setOnClickListener { onMesaClick(mesa) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Mesa>() {
        override fun areItemsTheSame(oldItem: Mesa, newItem: Mesa) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Mesa, newItem: Mesa) = oldItem == newItem
    }
}
