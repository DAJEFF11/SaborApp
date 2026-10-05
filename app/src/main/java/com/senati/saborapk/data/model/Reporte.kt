package com.senati.saborapk.data.model

data class ResumenVentas(
    val ventaDelDia: Double,
    val cantidadPedidos: Int,
    val ticketPromedio: Double
)

data class TopPlato(
    val nombre: String,
    val cantidad: Int
)

data class VentaPorMesa(
    val numeroMesa: Int,
    val total: Double
)
