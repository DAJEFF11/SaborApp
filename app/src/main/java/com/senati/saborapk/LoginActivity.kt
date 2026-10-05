package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.UsuarioDao
import com.senati.saborapk.data.SessionManager
import com.senati.saborapk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usuarioDao = UsuarioDao(this)
        sessionManager = SessionManager(this)

        if (sessionManager.sesionActiva) {
            abrirMenu(
                sessionManager.nombreUsuario ?: getString(R.string.usuario_generico),
                sessionManager.rol ?: ROL_MOZO
            )
            return
        }

        binding.btnIngresar.setOnClickListener { validarIngreso() }
        binding.etClave.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validarIngreso()
                true
            } else {
                false
            }
        }
    }

    private fun validarIngreso() {
        val usuario = binding.etUsuario.text?.toString()?.trim().orEmpty()
        val clave = binding.etClave.text?.toString().orEmpty()

        binding.tilUsuario.error = if (usuario.isBlank()) {
            getString(R.string.error_usuario_requerido)
        } else {
            null
        }
        binding.tilClave.error = if (clave.isBlank()) {
            getString(R.string.error_clave_requerida)
        } else {
            null
        }

        if (usuario.isBlank() || clave.isBlank()) return

        val usuarioValidado = usuarioDao.validarUsuario(usuario, clave)
        if (usuarioValidado != null) {
            sessionManager.guardar(usuarioValidado)
            abrirMenu(usuarioValidado.usuario, usuarioValidado.rol)
        } else {
            Toast.makeText(this, R.string.error_credenciales, Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirMenu(nombre: String, rol: String) {
        val intent = Intent(this, MenuActivity::class.java).apply {
            putExtra(MenuActivity.EXTRA_NOMBRE, nombre)
            putExtra(MenuActivity.EXTRA_ROL, rol)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val ROL_MOZO = "MOZO"
    }

}
