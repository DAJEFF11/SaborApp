package com.senati.saborapk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.senati.saborapk.data.PedidoDao
import com.senati.saborapk.databinding.ActivityCuentaBinding
import com.senati.saborapk.ui.DetallePedidoAdapter

class CuentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuentaBinding
    private lateinit var pedidoDao: PedidoDao
    private val detalleAdapter = DetallePedidoAdapter()
    private var pedidoId: Long = ID_INVALIDO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pedidoDao = PedidoDao(this)
        pedidoId = intent.getLongExtra(EXTRA_PEDIDO_ID, ID_INVALIDO)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvDetalleCuenta.apply {
            layoutManager = LinearLayoutManager(this@CuentaActivity)
            adapter = detalleAdapter
        }
        binding.btnCerrarCuenta.setOnClickListener { confirmarCierre() }
        cargarCuenta()
    }

    private fun cargarCuenta() {
        val pedido = pedidoDao.obtenerPedido(pedidoId) ?: run {
            Toast.makeText(this, R.string.error_pedido_no_encontrado, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.toolbar.title = getString(R.string.cuenta_mesa, pedido.numeroMesa)
        binding.tvDatosPedido.text = getString(R.string.datos_pedido, pedido.id, pedido.fecha)
        binding.tvTotalCuenta.text = getString(R.string.moneda, pedido.total)
        detalleAdapter.submitList(pedidoDao.listarDetalle(pedido.id))
        binding.btnCerrarCuenta.isEnabled = pedido.estado == PedidoDao.ESTADO_ABIERTO
    }

    private fun confirmarCierre() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cerrar_cuenta)
            .setMessage(R.string.confirmar_cerrar_cuenta)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.cerrar_cuenta) { _, _ -> cerrarCuenta() }
            .show()
    }

    private fun cerrarCuenta() {
        if (pedidoDao.cerrarCuenta(pedidoId)) {
            Toast.makeText(this, R.string.mesa_liberada, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            binding.btnCerrarCuenta.isEnabled = false
            Toast.makeText(this, R.string.error_cerrar_cuenta, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_PEDIDO_ID = "extra_pedido_id"
        private const val ID_INVALIDO = -1L
    }
}
