package com.senati.saborapk.data

import android.content.Context
import androidx.core.content.edit
import com.senati.saborapk.data.model.Usuario

class SessionManager(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    val sesionActiva: Boolean
        get() = preferences.getBoolean(KEY_ACTIVA, false)

    val nombreUsuario: String?
        get() = preferences.getString(KEY_USUARIO, null)

    val rol: String?
        get() = preferences.getString(KEY_ROL, null)

    fun guardar(usuario: Usuario) {
        preferences.edit {
            putBoolean(KEY_ACTIVA, true)
            putLong(KEY_USUARIO_ID, usuario.id)
            putString(KEY_USUARIO, usuario.usuario)
            putString(KEY_ROL, usuario.rol)
        }
    }

    fun cerrar() {
        preferences.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "saborapp_sesion"
        private const val KEY_ACTIVA = "activa"
        private const val KEY_USUARIO_ID = "usuario_id"
        private const val KEY_USUARIO = "usuario"
        private const val KEY_ROL = "rol"
    }
}
