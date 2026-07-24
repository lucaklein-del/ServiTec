package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.repository.TaulaRepository
import kotlinx.coroutines.launch

class PantallaPanell : AppCompatActivity() {

    // Cambiamos el nombre a tu repositorio real
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
        setContentView(R.layout.pantalla_panell) // Tu XML con el ConstraintLayout y los botones colocados
        // Configuración del botón de cerrar sesión
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
            // Llamamos a tu método 'obtenirTaules' que está dentro de tu estructura
            val listaMesas = repository.obtenirTaules()

            if (listaMesas != null) {
                // 🎇 RECORREMOS LAS MESAS QUE DEVUELVE TU API EN C#
                for (taula in listaMesas) {
                    // Buscamos si ese ID de la base de datos tiene un botón físico asignado en tu mapa
                    val resIdBoton = mapaBotonesMesa[taula.idTaula]

                    if (resIdBoton != null) {
                        // Si existe, lo configuramos inyectándole su estado dinámico actual
                        configurarMesa(
                            resId = resIdBoton,
                            idMesaBD = taula.idTaula,
                            nTaula = "Taula ${taula.numero}",
                            estaLliure = taula.estat
                        )
                    }
                }
            } else {
                Toast.makeText(this@PantallaPanell, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarMesa(resId: Int, idMesaBD: Int, nTaula: String, estaLliure: Boolean) {
        val botonMesa = findViewById<AppCompatButton>(resId) ?: return

        // 🎨 Aplicamos los fondos difuminados pastel según el booleano que viene de SQL Server
        if (!estaLliure) {
            botonMesa.setBackgroundResource(R.color.taula_ocupada2)
        }

        botonMesa.setOnClickListener {
            val intent = Intent(this, PantallaTaula::class.java).apply {
                putExtra("idTaula", idMesaBD)
                putExtra("nTaula", nTaula)
                putExtra("taulaOcupada", !estaLliure)
            }
            startActivity(intent)
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