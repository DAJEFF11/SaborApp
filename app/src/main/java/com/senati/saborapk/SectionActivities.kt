package com.senati.saborapk

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapk.data.ReporteDao
import com.senati.saborapk.data.model.ResumenVentas
import com.senati.saborapk.data.model.TopPlato
import com.senati.saborapk.data.model.VentaPorMesa
import com.senati.saborapk.databinding.ActivityReportesBinding
import com.senati.saborapk.ui.TopPlatoAdapter
import com.senati.saborapk.ui.VentaMesaAdapter

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var reporteDao: ReporteDao
    private val topAdapter = TopPlatoAdapter()
    private val ventaMesaAdapter = VentaMesaAdapter()
    private var resumen = ResumenVentas(0.0, 0, 0.0)
    private var topPlatos: List<TopPlato> = emptyList()
    private var ventasPorMesa: List<VentaPorMesa> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reporteDao = ReporteDao(this)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvTopPlatos.apply {
            layoutManager = LinearLayoutManager(this@ReportesActivity)
            adapter = topAdapter
            isNestedScrollingEnabled = false
        }
        binding.rvVentaMesas.apply {
            layoutManager = LinearLayoutManager(this@ReportesActivity)
            adapter = ventaMesaAdapter
            isNestedScrollingEnabled = false
        }
        binding.btnCompartirResumen.setOnClickListener { compartirResumen() }
    }

    override fun onResume() {
        super.onResume()
        cargarReportes()
    }

    private fun cargarReportes() {
        resumen = reporteDao.resumenDelDia()
        topPlatos = reporteDao.topPlatos()
        ventasPorMesa = reporteDao.ventaPorMesa()

        binding.tvVentaDia.text = getString(R.string.moneda, resumen.ventaDelDia)
        binding.tvCantidadPedidos.text = getString(R.string.numero_entero, resumen.cantidadPedidos)
        binding.tvTicketPromedio.text = getString(R.string.moneda, resumen.ticketPromedio)
        binding.tvSinVentas.visibility = if (resumen.cantidadPedidos == 0) View.VISIBLE else View.GONE
        topAdapter.mostrar(topPlatos)
        ventaMesaAdapter.submitList(ventasPorMesa)
    }

    private fun compartirResumen() {
        val texto = buildString {
            appendLine(getString(R.string.resumen_saborapp))
            appendLine(getString(R.string.resumen_venta, getString(R.string.moneda, resumen.ventaDelDia)))
            appendLine(getString(R.string.resumen_pedidos, resumen.cantidadPedidos))
            appendLine(getString(R.string.resumen_ticket, getString(R.string.moneda, resumen.ticketPromedio)))
            if (topPlatos.isNotEmpty()) {
                appendLine()
                appendLine(getString(R.string.platos_mas_pedidos))
                topPlatos.forEach { appendLine("${it.nombre}: ${it.cantidad}") }
            }
        }.trim()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texto)
        }
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.compartir_resumen)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.sin_apps_compartir, Toast.LENGTH_SHORT).show()
        }
    }
}
