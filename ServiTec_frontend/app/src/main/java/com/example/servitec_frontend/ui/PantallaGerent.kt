package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.servitec_frontend.R
import com.google.android.material.card.MaterialCardView

class PantallaGerent : AppCompatActivity() {
    private lateinit var btnTancarSessio: TextView
    private lateinit var afegirUsuari : MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gerent)
        btnTancarSessio = findViewById(R.id.btnTancarSessio)
        afegirUsuari = findViewById(R.id.cardAfegirUsuari)

        btnTancarSessio.setOnClickListener {
            tancarSessio()
        }

        afegirUsuari.setOnClickListener {
            startActivity(Intent(this, PantallaAfegirUsuari::class.java))
        }

    }
    private fun tancarSessio() {
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        // Netejar l'historial de pantalles perquè no pugui tornar enrere
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
