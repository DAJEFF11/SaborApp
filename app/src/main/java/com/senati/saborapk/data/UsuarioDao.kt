package com.senati.saborapk.data

import android.content.Context
import com.senati.saborapk.data.model.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DBHelper(context.applicationContext)

    fun validarUsuario(nombreUsuario: String, clave: String): Usuario? {
        val sql = """
            SELECT id, usuario, rol
            FROM usuario
            WHERE usuario = ? AND clave = ?
            LIMIT 1
        """.trimIndent()

        dbHelper.readableDatabase.rawQuery(sql, arrayOf(nombreUsuario, clave)).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return Usuario(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                usuario = cursor.getString(cursor.getColumnIndexOrThrow("usuario")),
                rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
            )
        }
    }
}
