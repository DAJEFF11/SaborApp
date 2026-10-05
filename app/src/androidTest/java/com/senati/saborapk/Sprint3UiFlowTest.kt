package com.senati.saborapk

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.senati.saborapk.data.DBHelper
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.data.PedidoDao
import com.senati.saborapk.data.PlatoDao
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Sprint3UiFlowTest {

    private lateinit var context: Context

    @Before
    fun prepararDatos() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(DBHelper.DB_NAME)
        PlatoDao(context).insertar("Lomo saltado", "Fondos", 25.0, true)
        MesaDao(context).insertar(4, 6)
    }

    @After
    fun limpiarDatos() {
        context.deleteDatabase(DBHelper.DB_NAME)
    }

    @Test
    fun tomaPedidoMuestraCuentaYCierraMesa() {
        ActivityScenario.launch(PedidoActivity::class.java).use {
            onView(withText("Mesa 4")).perform(click())
            onView(withId(R.id.btnAgregarPedido)).perform(click())

            onView(withText("1 x Lomo saltado")).check(matches(isDisplayed()))
            onView(allOf(withId(R.id.tvTotalPedido), withText("S/ 25.00")))
                .check(matches(isDisplayed()))

            onView(withId(R.id.btnVerCuenta)).perform(click())
            onView(withText("Cuenta · Mesa 4")).check(matches(isDisplayed()))
            onView(withId(R.id.btnCerrarCuenta)).perform(click())
            onView(withId(android.R.id.button1)).perform(click())

            assertEquals(MesaDao.ESTADO_LIBRE, MesaDao(context).listar().single().estado)
            assertEquals(
                PedidoDao.ESTADO_CERRADO,
                PedidoDao(context).obtenerPedido(1L)?.estado
            )
        }
    }
}
