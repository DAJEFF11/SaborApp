package com.senati.saborapk.data.model

data class Plato(
    val id: Long,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val disponible: Boolean
)
