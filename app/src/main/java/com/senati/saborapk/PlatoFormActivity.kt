package com.senati.saborapk

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.data.model.Plato
import com.senati.saborapk.databinding.ActivityPlatoFormBinding
import java.util.Locale

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private lateinit var platoDao: PlatoDao
    private var platoId: Long = ID_NUEVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        platoDao = PlatoDao(this)
        platoId = intent.getLongExtra(EXTRA_PLATO_ID, ID_NUEVO)

        binding.toolbar.setNavigationOnClickListener { finish() }
        configurarCategorias()
        if (platoId != ID_NUEVO) cargarPlato()
        binding.btnGuardarPlato.setOnClickListener { guardarPlato() }
        binding.btnEliminarPlato.setOnClickListener { confirmarEliminacion() }
    }

    private fun cargarPlato() {
        val plato = platoDao.obtener(platoId) ?: run {
            Toast.makeText(this, R.string.error_plato_no_encontrado, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.toolbar.setTitle(R.string.editar_plato)
        binding.etNombrePlato.setText(plato.nombre)
        binding.etPrecioPlato.setText(String.format(Locale.getDefault(), "%.2f", plato.precio))
        binding.switchDisponible.isChecked = plato.disponible
        val categorias = resources.getStringArray(R.array.categorias_plato)
        val posicion = categorias.indexOf(plato.categoria).coerceAtLeast(0)
        binding.spinnerCategoria.setSelection(posicion)
        binding.btnGuardarPlato.setText(R.string.actualizar)
        binding.btnEliminarPlato.visibility = View.VISIBLE
    }

    private fun configurarCategorias() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.categorias_plato,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerCategoria.adapter = adapter
    }

    private fun guardarPlato() {
        val nombre = binding.etNombrePlato.text?.toString()?.trim().orEmpty()
        val precioTexto = binding.etPrecioPlato.text?.toString()?.trim().orEmpty()

        binding.tilNombrePlato.error = if (nombre.isBlank()) {
            getString(R.string.error_nombre_requerido)
        } else {
            null
        }
        binding.tilPrecioPlato.error = if (precioTexto.isBlank()) {
            getString(R.string.error_precio_requerido)
        } else {
            null
        }

        if (nombre.isBlank() || precioTexto.isBlank()) return

        val precio = precioTexto.replace(',', '.').toDoubleOrNull()
        if (precio == null || precio <= 0.0) {
            binding.tilPrecioPlato.error = getString(R.string.error_precio_invalido)
            return
        }

        try {
            if (platoId == ID_NUEVO) {
                platoDao.insertar(
                    nombre = nombre,
                    categoria = binding.spinnerCategoria.selectedItem.toString(),
                    precio = precio,
                    disponible = binding.switchDisponible.isChecked
                )
            } else {
                platoDao.actualizar(
                    Plato(
                        id = platoId,
                        nombre = nombre,
                        categoria = binding.spinnerCategoria.selectedItem.toString(),
                        precio = precio,
                        disponible = binding.switchDisponible.isChecked
                    )
                )
            }
            Toast.makeText(
                this,
                if (platoId == ID_NUEVO) R.string.plato_registrado else R.string.plato_actualizado,
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } catch (_: SQLiteException) {
            Toast.makeText(this, R.string.error_guardar_plato, Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(R.string.eliminar_plato)
            .setMessage(R.string.confirmar_eliminar_plato)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ -> eliminarPlato() }
            .show()
    }

    private fun eliminarPlato() {
        try {
            platoDao.eliminar(platoId)
            Toast.makeText(this, R.string.plato_eliminado, Toast.LENGTH_SHORT).show()
            finish()
        } catch (_: SQLiteConstraintException) {
            Toast.makeText(this, R.string.error_plato_con_pedidos, Toast.LENGTH_LONG).show()
        } catch (_: SQLiteException) {
            Toast.makeText(this, R.string.error_eliminar_plato, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_PLATO_ID = "extra_plato_id"
        private const val ID_NUEVO = -1L
    }
}
