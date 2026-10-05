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

    fun obtener(id: Long): Plato? {
        val sql = """
            SELECT id, nombre, categoria, precio, disponible
            FROM plato
            WHERE id = ?
            LIMIT 1
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, arrayOf(id.toString())).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return cursorAPlato(cursor)
        }
    }

    fun actualizar(plato: Plato): Int {
        val values = ContentValues().apply {
            put("nombre", plato.nombre)
            put("categoria", plato.categoria)
            put("precio", plato.precio)
            put("disponible", if (plato.disponible) 1 else 0)
        }
        return dbHelper.writableDatabase.update(
            "plato",
            values,
            "id = ?",
            arrayOf(plato.id.toString())
        )
    }

    fun eliminar(id: Long): Int {
        return dbHelper.writableDatabase.delete("plato", "id = ?", arrayOf(id.toString()))
    }

    fun listar(filtro: String = ""): List<Plato> {
        val platos = mutableListOf<Plato>()
        val baseSql = """
            SELECT id, nombre, categoria, precio, disponible
            FROM plato
        """.trimIndent()
        val tieneFiltro = filtro.isNotBlank()
        val sql = buildString {
            append(baseSql)
            if (tieneFiltro) append(" WHERE nombre LIKE ?")
            append(" ORDER BY categoria COLLATE NOCASE, nombre COLLATE NOCASE")
        }
        val args = if (tieneFiltro) arrayOf("%${filtro.trim()}%") else null

        dbHelper.readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                platos += cursorAPlato(cursor)
            }
        }
        return platos
    }

    fun listarDisponibles(): List<Plato> {
        val platos = mutableListOf<Plato>()
        val sql = """
            SELECT id, nombre, categoria, precio, disponible
            FROM plato
            WHERE disponible = 1
            ORDER BY categoria COLLATE NOCASE, nombre COLLATE NOCASE
        """.trimIndent()
        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                platos += cursorAPlato(cursor)
            }
        }
        return platos
    }

    private fun cursorAPlato(cursor: android.database.Cursor) = Plato(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
        categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
        disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1
    )
}
