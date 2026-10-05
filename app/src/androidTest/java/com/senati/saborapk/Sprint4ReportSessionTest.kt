package com.senati.saborapk

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.senati.saborapk.data.DBHelper
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.PedidoDao
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.data.ReporteDao
import com.senati.saborapk.data.SessionManager
import com.senati.saborapk.data.model.Usuario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Sprint4ReportSessionTest {

    private lateinit var context: Context

    @Before
    fun prepararDatos() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DBHelper.DB_NAME)
        SessionManager(context).cerrar()
    }

    @After
    fun limpiarDatos() {
        context.deleteDatabase(DBHelper.DB_NAME)
        SessionManager(context).cerrar()
    }

    @Test
    fun reportesAgrupanVentasCerradasDelDia() {
        val platoDao = PlatoDao(context)
        val mesaDao = MesaDao(context)
        val pedidoDao = PedidoDao(context)
        val reporteDao = ReporteDao(context)
        val lomoId = platoDao.insertar("Lomo saltado", "Fondos", 25.0, true)
        val chichaId = platoDao.insertar("Chicha morada", "Bebidas", 12.0, true)
        val mesaUnoId = mesaDao.insertar(1, 4)
        val mesaDosId = mesaDao.insertar(2, 4)

        val primerPedido = pedidoDao.agregarPlato(mesaUnoId, lomoId, 2)
        pedidoDao.agregarPlato(mesaUnoId, chichaId, 1)
        assertTrue(pedidoDao.cerrarCuenta(primerPedido))

        val segundoPedido = pedidoDao.agregarPlato(mesaDosId, lomoId, 1)
        assertTrue(pedidoDao.cerrarCuenta(segundoPedido))

        val resumen = reporteDao.resumenDelDia()
        assertEquals(87.0, resumen.ventaDelDia, 0.001)
        assertEquals(2, resumen.cantidadPedidos)
        assertEquals(43.5, resumen.ticketPromedio, 0.001)

        val top = reporteDao.topPlatos()
        assertEquals("Lomo saltado", top.first().nombre)
        assertEquals(3, top.first().cantidad)
        assertEquals(2, reporteDao.ventaPorMesa().size)
        assertEquals(62.0, reporteDao.ventaPorMesa().first().total, 0.001)
    }

    @Test
    fun reporteSinVentasDevuelveCeros() {
        val resumen = ReporteDao(context).resumenDelDia()

        assertEquals(0.0, resumen.ventaDelDia, 0.001)
        assertEquals(0, resumen.cantidadPedidos)
        assertEquals(0.0, resumen.ticketPromedio, 0.001)
        assertTrue(ReporteDao(context).topPlatos().isEmpty())
        assertTrue(ReporteDao(context).ventaPorMesa().isEmpty())
    }

    @Test
    fun sesionSeRecuerdaYSeEliminaAlSalir() {
        val sessionManager = SessionManager(context)
        sessionManager.guardar(Usuario(1L, "admin", "ADMIN"))

        assertTrue(sessionManager.sesionActiva)
        assertEquals("admin", sessionManager.nombreUsuario)
        assertEquals("ADMIN", sessionManager.rol)

        sessionManager.cerrar()

        assertFalse(sessionManager.sesionActiva)
        assertEquals(null, sessionManager.nombreUsuario)
        assertEquals(null, sessionManager.rol)
    }
}
