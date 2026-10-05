package com.senati.saborapk

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.senati.saborapk.data.DBHelper
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.PedidoDao
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.data.model.Plato
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Sprint3DatabaseTest {

    private lateinit var context: Context

    @Before
    fun prepararBaseLimpia() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DBHelper.DB_NAME)
    }

    @After
    fun limpiarBase() {
        context.deleteDatabase(DBHelper.DB_NAME)
    }

    @Test
    fun migraDeVersionUnoSinPerderCatalogos() {
        val archivoDb = context.getDatabasePath(DBHelper.DB_NAME)
        archivoDb.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(archivoDb, null).use { db ->
            db.execSQL("CREATE TABLE usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario TEXT, clave TEXT, rol TEXT)")
            db.execSQL("CREATE TABLE plato (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, categoria TEXT, precio REAL, disponible INTEGER)")
            db.execSQL("CREATE TABLE mesa (id INTEGER PRIMARY KEY AUTOINCREMENT, numero INTEGER UNIQUE, capacidad INTEGER, estado TEXT)")
            db.execSQL("INSERT INTO usuario(usuario, clave, rol) VALUES ('admin', '1234', 'ADMIN')")
            db.execSQL("INSERT INTO plato(nombre, categoria, precio, disponible) VALUES ('Pollo a la brasa', 'Fondos', 45, 1)")
            db.execSQL("INSERT INTO mesa(numero, capacidad, estado) VALUES (1, 4, 'LIBRE')")
            db.version = 1
        }

        DBHelper(context).writableDatabase.use { dbMigrada ->
            assertEquals(2, dbMigrada.version)
            assertEquals(1, contar(dbMigrada, "plato"))
            assertEquals(1, contar(dbMigrada, "mesa"))
            assertTrue(existeTabla(dbMigrada, "pedido"))
            assertTrue(existeTabla(dbMigrada, "detalle_pedido"))
        }
    }

    @Test
    fun pedidoGuardaPrecioHistoricoCalculaTotalYCierraCuenta() {
        val platoDao = PlatoDao(context)
        val mesaDao = MesaDao(context)
        val pedidoDao = PedidoDao(context)
        val idPlato = platoDao.insertar("Lomo saltado", "Fondos", 25.0, true)
        val idMesa = mesaDao.insertar(4, 6)

        val idPedido = pedidoDao.agregarPlato(idMesa, idPlato, 2)

        assertEquals(MesaDao.ESTADO_OCUPADA, mesaDao.listar().single().estado)
        assertEquals(50.0, pedidoDao.obtenerPedido(idPedido)?.total ?: 0.0, 0.001)
        val detalle = pedidoDao.listarDetalle(idPedido).single()
        assertEquals(25.0, detalle.precioUnitario, 0.001)
        assertEquals(50.0, detalle.subtotal, 0.001)

        platoDao.actualizar(
            Plato(idPlato, "Lomo saltado", "Fondos", 30.0, true)
        )
        assertEquals(25.0, pedidoDao.listarDetalle(idPedido).single().precioUnitario, 0.001)

        assertTrue(pedidoDao.cerrarCuenta(idPedido))
        assertEquals(PedidoDao.ESTADO_CERRADO, pedidoDao.obtenerPedido(idPedido)?.estado)
        assertEquals(MesaDao.ESTADO_LIBRE, mesaDao.listar().single().estado)
    }

    @Test
    fun errorAlAgregarNoDejaPedidoNiMesaOcupada() {
        val platoDao = PlatoDao(context)
        val mesaDao = MesaDao(context)
        val pedidoDao = PedidoDao(context)
        val idPlato = platoDao.insertar("Agotado", "Fondos", 10.0, false)
        val idMesa = mesaDao.insertar(2, 4)

        var errorDetectado = false
        try {
            pedidoDao.agregarPlato(idMesa, idPlato, 1)
        } catch (_: IllegalStateException) {
            errorDetectado = true
        }

        assertTrue(errorDetectado)
        assertEquals(MesaDao.ESTADO_LIBRE, mesaDao.listar().single().estado)
        assertEquals(null, pedidoDao.obtenerPedidoAbiertoPorMesa(idMesa))
    }

    @Test
    fun buscaActualizaYProtegePlatosConPedidos() {
        val platoDao = PlatoDao(context)
        val mesaDao = MesaDao(context)
        val pedidoDao = PedidoDao(context)
        val idPlato = platoDao.insertar("Chicha morada", "Bebidas", 8.0, true)

        assertEquals(1, platoDao.listar("chicha").size)
        assertEquals(0, platoDao.listar("ceviche").size)

        platoDao.actualizar(Plato(idPlato, "Chicha morada 1L", "Bebidas", 10.0, true))
        assertEquals("Chicha morada 1L", platoDao.obtener(idPlato)?.nombre)

        val idMesa = mesaDao.insertar(8, 4)
        pedidoDao.agregarPlato(idMesa, idPlato, 1)

        var protegido = false
        try {
            platoDao.eliminar(idPlato)
        } catch (_: SQLiteConstraintException) {
            protegido = true
        }
        assertTrue(protegido)
        assertNotNull(platoDao.obtener(idPlato))
    }

    private fun contar(db: SQLiteDatabase, tabla: String): Int {
        return db.rawQuery("SELECT COUNT(*) FROM $tabla", null).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }
    }

    private fun existeTabla(db: SQLiteDatabase, tabla: String): Boolean {
        return db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
            arrayOf(tabla)
        ).use { it.moveToFirst() }
    }
}
