package com.senati.saborapk

import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.databinding.ActivityPlatoFormBinding

class PlatoFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatoFormBinding
    private lateinit var platoDao: PlatoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        platoDao = PlatoDao(this)

        binding.toolbar.setNavigationOnClickListener { finish() }
        configurarCategorias()
        binding.btnGuardarPlato.setOnClickListener { guardarPlato() }
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
            platoDao.insertar(
                nombre = nombre,
                categoria = binding.spinnerCategoria.selectedItem.toString(),
                precio = precio,
                disponible = binding.switchDisponible.isChecked
            )
            Toast.makeText(this, R.string.plato_registrado, Toast.LENGTH_SHORT).show()
            finish()
        } catch (_: SQLiteException) {
            Toast.makeText(this, R.string.error_guardar_plato, Toast.LENGTH_SHORT).show()
        }
    }
}
