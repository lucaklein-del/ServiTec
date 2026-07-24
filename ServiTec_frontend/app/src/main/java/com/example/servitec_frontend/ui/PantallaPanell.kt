package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.repository.TaulaRepository
import kotlinx.coroutines.launch

class PantallaPanell : AppCompatActivity() {

    private val repository = TaulaRepository()
    private val mapaBotonesMesa = mapOf(
        2 to R.id.taula2,
        4 to R.id.taula4,
        5 to R.id.taula5,
        6 to R.id.taula6,
        7 to R.id.taula7,
        8 to R.id.taula8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)

        val btnCerrarSesion = findViewById<View>(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMesasDesdeBD()
    }

    private fun cargarMesasDesdeBD() {
        lifecycleScope.launch {
            val listaMesas = repository.obtenirTaules()

            if (listaMesas != null) {
                for (taula in listaMesas) {
                    val resIdBoton = mapaBotonesMesa[taula.idTaula]

                    if (resIdBoton != null) {
                        // 🎯 Le pasamos el 'estatComanda' (o 'estat') que viene del backend
                        configurarMesa(
                            resId = resIdBoton,
                            idMesaBD = taula.idTaula,
                            nTaula = "Taula ${taula.numero}",
                            estatComanda = taula.estatComanda ?: "lliure" // Ej: "oberta", "Pendent", "lliure"
                        )
                    }
                }
            } else {
                Toast.makeText(this@PantallaPanell, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarMesa(resId: Int, idMesaBD: Int, nTaula: String, estatComanda: String) {
        val botonMesa = findViewById<AppCompatButton>(resId) ?: return

        // 🎨 Pintamos la mesa en función del estado de su comanda activa
        when (estatComanda) {
            "oberta" -> {
                botonMesa.isSelected = true
                botonMesa.isActivated = false
            }
            "pendent" -> {
                botonMesa.isSelected = false
                botonMesa.isActivated = true
            }
            else -> {
                botonMesa.isSelected = false
                botonMesa.isActivated = false
            }
        }

        val esOcupada = estatComanda == "oberta" || estatComanda == "pendent"

        botonMesa.setOnClickListener {
            val intent = Intent(this, PantallaTaula::class.java).apply {
                putExtra("idTaula", idMesaBD)
                putExtra("nTaula", nTaula)
                putExtra("taulaOcupada", esOcupada)
                putExtra("estatComanda", estatComanda)
            }
            startActivity(intent)
        }
    }

    private fun tancarSessio() {
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}