package com.senati.saborapk.data

import android.content.ContentValues
import android.content.Context
import com.senati.saborapk.data.model.Mesa

class MesaDao(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    fun insertar(numero: Int, capacidad: Int): Long {
        val values = ContentValues().apply {
            put("numero", numero)
            put("capacidad", capacidad)
            put("estado", ESTADO_LIBRE)
        }
        return dbHelper.writableDatabase.insertOrThrow("mesa", null, values)
    }

    fun listar(): List<Mesa> {
        val mesas = mutableListOf<Mesa>()
        val sql = """
            SELECT id, numero, capacidad, estado
            FROM mesa
            ORDER BY numero
        """.trimIndent()

        dbHelper.readableDatabase.rawQuery(sql, null).use { cursor ->
            while (cursor.moveToNext()) {
                mesas += Mesa(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    numero = cursor.getInt(cursor.getColumnIndexOrThrow("numero")),
                    capacidad = cursor.getInt(cursor.getColumnIndexOrThrow("capacidad")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
                )
            }
        }
        return mesas
    }

    companion object {
        const val ESTADO_LIBRE = "LIBRE"
    }
}
