package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.UsuarioDao
import com.senati.saborapk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usuarioDao = UsuarioDao(this)

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
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra(MenuActivity.EXTRA_NOMBRE, usuarioValidado.usuario)
                putExtra(MenuActivity.EXTRA_ROL, usuarioValidado.rol)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, R.string.error_credenciales, Toast.LENGTH_SHORT).show()
        }
    }

}
