package com.senati.saborapk

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.PedidoDao
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.data.model.Mesa
import com.senati.saborapk.data.model.Pedido
import com.senati.saborapk.data.model.Plato
import com.senati.saborapk.databinding.ActivityPedidoBinding
import com.senati.saborapk.ui.DetallePedidoAdapter
import com.senati.saborapk.ui.MesaPedidoAdapter

class PedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoBinding
    private lateinit var mesaDao: MesaDao
    private lateinit var platoDao: PlatoDao
    private lateinit var pedidoDao: PedidoDao
    private lateinit var mesaAdapter: MesaPedidoAdapter
    private val detalleAdapter = DetallePedidoAdapter()

    private var mesaSeleccionada: Mesa? = null
    private var pedidoActual: Pedido? = null
    private var platosDisponibles: List<Plato> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mesaDao = MesaDao(this)
        platoDao = PlatoDao(this)
        pedidoDao = PedidoDao(this)
        mesaAdapter = MesaPedidoAdapter(::seleccionarMesa)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvMesasPedido.apply {
            layoutManager = GridLayoutManager(this@PedidoActivity, 3)
            adapter = mesaAdapter
        }
        binding.rvDetallePedido.apply {
            layoutManager = LinearLayoutManager(this@PedidoActivity)
            adapter = detalleAdapter
        }
        binding.btnAgregarPedido.setOnClickListener { agregarPlato() }
        binding.btnVerCuenta.setOnClickListener { abrirCuenta() }
    }

    override fun onResume() {
        super.onResume()
        cargarPantalla()
    }

    private fun cargarPantalla() {
        val mesas = mesaDao.listar()
        mesaAdapter.submitList(mesas)
        binding.tvSinMesas.visibility = if (mesas.isEmpty()) View.VISIBLE else View.GONE

        mesaSeleccionada = mesaSeleccionada?.let { seleccionada ->
            mesas.firstOrNull { it.id == seleccionada.id }
        }
        mesaAdapter.mesaSeleccionadaId = mesaSeleccionada?.id
        cargarPlatosDisponibles()
        actualizarPedidoSeleccionado()
    }

    private fun cargarPlatosDisponibles() {
        platosDisponibles = platoDao.listarDisponibles()
        val nombres = platosDisponibles.map {
            getString(R.string.plato_spinner, it.nombre, it.precio)
        }
        binding.spinnerPlatoPedido.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            nombres
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerPlatoPedido.isEnabled = platosDisponibles.isNotEmpty()
    }

    private fun seleccionarMesa(mesa: Mesa) {
        mesaSeleccionada = mesa
        mesaAdapter.mesaSeleccionadaId = mesa.id
        actualizarPedidoSeleccionado()
    }

    private fun actualizarPedidoSeleccionado() {
        val mesa = mesaSeleccionada
        binding.cardPedido.visibility = if (mesa == null) View.GONE else View.VISIBLE
        if (mesa == null) return

        pedidoActual = pedidoDao.obtenerPedidoAbiertoPorMesa(mesa.id)
        val detalles = pedidoActual?.let { pedidoDao.listarDetalle(it.id) }.orEmpty()
        detalleAdapter.submitList(detalles)

        binding.tvMesaSeleccionada.text = getString(
            if (pedidoActual == null) R.string.mesa_pedido_nuevo else R.string.mesa_pedido_abierto,
            mesa.numero
        )
        binding.tvPedidoVacio.visibility = if (detalles.isEmpty()) View.VISIBLE else View.GONE
        binding.tvTotalPedido.text = getString(R.string.moneda, pedidoActual?.total ?: 0.0)
        binding.btnAgregarPedido.isEnabled = platosDisponibles.isNotEmpty()
        binding.btnVerCuenta.isEnabled = pedidoActual != null && detalles.isNotEmpty()
    }

    private fun agregarPlato() {
        val mesa = mesaSeleccionada ?: return
        val plato = platosDisponibles.getOrNull(binding.spinnerPlatoPedido.selectedItemPosition)
            ?: run {
                Toast.makeText(this, R.string.sin_platos_disponibles, Toast.LENGTH_SHORT).show()
                return
            }
        val cantidad = binding.etCantidadPedido.text?.toString()?.toIntOrNull()
        binding.tilCantidadPedido.error = if (cantidad == null || cantidad <= 0) {
            getString(R.string.error_cantidad_invalida)
        } else {
            null
        }
        if (cantidad == null || cantidad <= 0) return

        try {
            pedidoDao.agregarPlato(mesa.id, plato.id, cantidad)
            binding.etCantidadPedido.setText(R.string.cantidad_inicial)
            Toast.makeText(this, R.string.plato_agregado_pedido, Toast.LENGTH_SHORT).show()
            cargarPantalla()
        } catch (error: IllegalStateException) {
            Toast.makeText(this, error.message ?: getString(R.string.error_guardar_pedido), Toast.LENGTH_LONG).show()
            cargarPantalla()
        } catch (_: SQLiteException) {
            Toast.makeText(this, R.string.error_guardar_pedido, Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirCuenta() {
        val pedido = pedidoActual ?: return
        startActivity(
            Intent(this, CuentaActivity::class.java)
                .putExtra(CuentaActivity.EXTRA_PEDIDO_ID, pedido.id)
        )
    }
}
