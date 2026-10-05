package com.senati.saborapk.data

import android.content.ContentValues
import android.content.Context
import com.senati.saborapk.data.model.Plato

class PlatoDao(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    fun insertar(
        nombre: String,
        categoria: String,
        precio: Double,
        disponible: Boolean
    ): Long {
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("categoria", categoria)
            put("precio", precio)
            put("disponible", if (disponible) 1 else 0)
        }
        return dbHelper.writableDatabase.insertOrThrow("plato", null, values)
    }

    fun listar(): List<Plato> {
        val platos = mutableListOf<Plato>()
        val sql = """
            SELECT id, nombre, categoria, precio, disponible
            FROM plato
            ORDER BY categoria COLLATE NOCASE, nombre COLLATE NOCASE
        """.trimIndent()

        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                platos += Plato(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1
                )
            }
        }
        return platos
    }
}
