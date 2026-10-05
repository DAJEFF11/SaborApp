package com.senati.saborapk

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.databinding.ActivityMesaFormBinding

class MesaFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesaFormBinding
    private lateinit var mesaDao: MesaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mesaDao = MesaDao(this)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnGuardarMesa.setOnClickListener { guardarMesa() }
    }

    private fun guardarMesa() {
        val numeroTexto = binding.etNumeroMesa.text?.toString()?.trim().orEmpty()
        val capacidadTexto = binding.etCapacidadMesa.text?.toString()?.trim().orEmpty()

        val numero = numeroTexto.toIntOrNull()
        val capacidad = capacidadTexto.toIntOrNull()

        binding.tilNumeroMesa.error = if (numero == null || numero <= 0) {
            getString(R.string.error_numero_mesa)
        } else {
            null
        }
        binding.tilCapacidadMesa.error = if (capacidad == null || capacidad !in 1..12) {
            getString(R.string.error_capacidad_invalida)
        } else {
            null
        }

        if (numero == null || numero <= 0 || capacidad == null || capacidad !in 1..12) return

        try {
            mesaDao.insertar(numero, capacidad)
            Toast.makeText(this, R.string.mesa_registrada, Toast.LENGTH_SHORT).show()
            finish()
        } catch (_: SQLiteConstraintException) {
            binding.tilNumeroMesa.error = getString(R.string.error_mesa_duplicada)
        } catch (_: SQLiteException) {
            Toast.makeText(this, R.string.error_guardar_mesa, Toast.LENGTH_SHORT).show()
        }
    }
}
