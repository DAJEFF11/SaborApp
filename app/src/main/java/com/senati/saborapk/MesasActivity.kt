package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.senati.saborapk.data.MesaDao
import com.senati.saborapk.databinding.ActivityMesasBinding
import com.senati.saborapk.ui.MesaAdapter

class MesasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMesasBinding
    private lateinit var mesaDao: MesaDao
    private val mesaAdapter = MesaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMesasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mesaDao = MesaDao(this)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvMesas.apply {
            layoutManager = GridLayoutManager(this@MesasActivity, 3)
            adapter = mesaAdapter
            setHasFixedSize(true)
        }
        binding.fabAgregarMesa.setOnClickListener {
            startActivity(Intent(this, MesaFormActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMesas()
    }

    private fun cargarMesas() {
        val mesas = mesaDao.listar()
        mesaAdapter.submitList(mesas)
        binding.tvMesasVacio.visibility = if (mesas.isEmpty()) View.VISIBLE else View.GONE
    }
}
