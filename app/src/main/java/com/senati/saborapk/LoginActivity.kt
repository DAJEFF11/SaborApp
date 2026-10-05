package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener { validarIngreso() }
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

        if (usuario == USUARIO_DEMO && clave == CLAVE_DEMO) {
            val intent = Intent(this, MenuActivity::class.java).apply {
                putExtra(MenuActivity.EXTRA_NOMBRE, usuario)
                putExtra(MenuActivity.EXTRA_ROL, ROL_ADMIN)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, R.string.error_credenciales, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val USUARIO_DEMO = "admin"
        private const val CLAVE_DEMO = "1234"
        private const val ROL_ADMIN = "ADMIN"
    }
}
