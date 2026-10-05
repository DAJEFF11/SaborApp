package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.SessionManager
import com.senati.saborapk.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        val nombre = intent.getStringExtra(EXTRA_NOMBRE)
            ?: sessionManager.nombreUsuario
            ?: getString(R.string.usuario_generico)
        val rol = intent.getStringExtra(EXTRA_ROL)
            ?: sessionManager.rol
            ?: ROL_MOZO

        binding.tvSaludo.text = getString(R.string.saludo_usuario, nombre)
        val rolVisible = getString(
            if (rol == ROL_ADMIN) R.string.rol_administrador else R.string.rol_mozo
        )
        binding.tvRol.text = getString(R.string.rol_usuario, rolVisible)
        binding.tvAvatar.text = nombre.firstOrNull()?.uppercase() ?: getString(R.string.avatar_default)
        binding.cardReportes.visibility = if (rol == ROL_ADMIN) View.VISIBLE else View.GONE

        binding.cardPlatos.setOnClickListener { abrir(PlatosActivity::class.java) }
        binding.cardMesas.setOnClickListener { abrir(MesasActivity::class.java) }
        binding.cardPedidos.setOnClickListener { abrir(PedidoActivity::class.java) }
        binding.cardReportes.setOnClickListener { abrir(ReportesActivity::class.java) }
        binding.btnSalir.setOnClickListener { cerrarSesion() }
    }

    private fun abrir(destino: Class<*>) {
        startActivity(Intent(this, destino))
    }

    private fun cerrarSesion() {
        sessionManager.cerrar()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_ROL = "extra_rol"
        private const val ROL_ADMIN = "ADMIN"
        private const val ROL_MOZO = "MOZO"
    }
}
