package com.senati.saborapk.data.model

data class DetallePedido(
    val id: Long,
    val idPlato: Long,
    val nombrePlato: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)
