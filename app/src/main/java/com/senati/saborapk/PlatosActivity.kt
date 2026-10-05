package com.senati.saborapk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.saborapk.data.PlatoDao
import com.senati.saborapk.databinding.ActivityPlatosBinding
import com.senati.saborapk.ui.PlatoAdapter

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    private lateinit var platoDao: PlatoDao
    private val platoAdapter = PlatoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        platoDao = PlatoDao(this)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvPlatos.apply {
            layoutManager = LinearLayoutManager(this@PlatosActivity)
            adapter = platoAdapter
            setHasFixedSize(true)
        }
        binding.fabAgregarPlato.setOnClickListener {
            startActivity(Intent(this, PlatoFormActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPlatos()
    }

    private fun cargarPlatos() {
        val platos = platoDao.listar()
        platoAdapter.submitList(platos)
        binding.tvPlatosVacio.visibility = if (platos.isEmpty()) View.VISIBLE else View.GONE
    }
}
