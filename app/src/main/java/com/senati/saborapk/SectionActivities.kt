package com.senati.saborapk

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.senati.saborapk.databinding.ActivitySectionBinding

abstract class SectionActivity : AppCompatActivity() {

    @get:StringRes
    protected abstract val titleRes: Int

    @get:StringRes
    protected abstract val badgeRes: Int

    @get:StringRes
    protected abstract val messageRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setTitle(titleRes)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.tvSectionBadge.setText(badgeRes)
        binding.tvSectionMessage.setText(messageRes)
    }
}

class PlatosActivity : SectionActivity() {
    override val titleRes = R.string.titulo_platos
    override val badgeRes = R.string.badge_platos
    override val messageRes = R.string.mensaje_platos_vacio
}

class MesasActivity : SectionActivity() {
    override val titleRes = R.string.titulo_mesas
    override val badgeRes = R.string.badge_mesas
    override val messageRes = R.string.mensaje_mesas_vacio
}

class PedidoActivity : SectionActivity() {
    override val titleRes = R.string.titulo_pedidos
    override val badgeRes = R.string.badge_pedidos
    override val messageRes = R.string.mensaje_pedidos_vacio
}

class ReportesActivity : SectionActivity() {
    override val titleRes = R.string.titulo_reportes
    override val badgeRes = R.string.badge_reportes
    override val messageRes = R.string.mensaje_reportes_vacio
}
