package com.senati.saborapk.data.model

data class Pedido(
    val id: Long,
    val idMesa: Long,
    val numeroMesa: Int,
    val fecha: String,
    val estado: String,
    val total: Double
)
