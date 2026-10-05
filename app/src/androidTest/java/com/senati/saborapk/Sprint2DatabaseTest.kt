package com.senati.saborapk

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.senati.saborapk.data.DBHelper
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.data.UsuarioDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Sprint2DatabaseTest {

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
    fun creaUsuariosYValidaCredenciales() {
        val usuarioDao = UsuarioDao(context)

        assertEquals("ADMIN", usuarioDao.validarUsuario("admin", "1234")?.rol)
        assertEquals("MOZO", usuarioDao.validarUsuario("mozo", "1234")?.rol)
        assertNull(usuarioDao.validarUsuario("admin", "incorrecta"))
    }

    @Test
    fun platosPersistenYSeOrdenanPorCategoriaYNombre() {
        PlatoDao(context).apply {
            insertar("Lomo saltado", "Fondos", 25.0, true)
            insertar("Chicha morada", "Bebidas", 12.0, true)
        }

        val platosDesdeNuevaInstancia = PlatoDao(context).listar()

        assertEquals(2, platosDesdeNuevaInstancia.size)
        assertEquals("Chicha morada", platosDesdeNuevaInstancia[0].nombre)
        assertEquals("Lomo saltado", platosDesdeNuevaInstancia[1].nombre)
    }

    @Test
    fun mesaNuevaEsLibreYRechazaNumeroDuplicado() {
        val mesaDao = MesaDao(context)
        mesaDao.insertar(numero = 4, capacidad = 6)

        val mesaGuardada = MesaDao(context).listar().single()
        assertEquals("LIBRE", mesaGuardada.estado)

        var restriccionDetectada = false
        try {
            mesaDao.insertar(numero = 4, capacidad = 2)
        } catch (_: SQLiteConstraintException) {
            restriccionDetectada = true
        }
        assertTrue(restriccionDetectada)
    }
}
