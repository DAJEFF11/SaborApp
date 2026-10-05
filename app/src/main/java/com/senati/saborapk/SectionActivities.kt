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

class ReportesActivity : SectionActivity() {
    override val titleRes = R.string.titulo_reportes
    override val badgeRes = R.string.badge_reportes
    override val messageRes = R.string.mensaje_reportes_vacio
}
