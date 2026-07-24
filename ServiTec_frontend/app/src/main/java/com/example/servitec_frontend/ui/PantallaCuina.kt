package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.ui.adapters.CuinaAdapter
import kotlinx.coroutines.launch

class PantallaCuina : AppCompatActivity() {
    private lateinit var rvComandes: RecyclerView
    private lateinit var btnCerrarSesion: TextView

    // Handler i Runnable per gestionar el refresc automàtic de comandes
    private val handler = Handler(Looper.getMainLooper())
    private val intervalRefresc = 10000L // 10 segons

    private val runnableRefresc = object : Runnable {
        override fun run() {
            carregarComandesCuina()
            handler.postDelayed(this, intervalRefresc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_cuina)

        // 1. Inicialitzar elements de la UI
        rvComandes = findViewById(R.id.rvComandesCuina)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Configurar el RecyclerView en Horitzontal per veure les taules com a "tickets"
        rvComandes.layoutManager = GridLayoutManager(this, 4)

        // 2. Configurar el botó de tancar sessió de la barra lateral
        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    override fun onResume() {
        super.onResume()
        // Iniciar el bucle de refresc automàtic en tornar a la pantalla
        handler.post(runnableRefresc)
    }

    override fun onPause() {
        super.onPause()
        // Aturar el bucle quan la pantalla no estigui activa per estalviar bateria i memòria
        handler.removeCallbacks(runnableRefresc)
    }

    /**
     * Mètode encarregat de cridar la API i actualitzar el RecyclerView de cuina.
     */
    private fun carregarComandesCuina() {
        lifecycleScope.launch {
            try {
                // 1. Llamamos al endpoint de la API que creamos en C#
                val response = RetrofitClient.instance.getComandesCuina()

                if (response.isSuccessful && response.body() != null) {
                    val llistaComandes = response.body()!!

                    // 2. Creamos el adaptador con los datos recibidos y se lo asignamos al RecyclerView
                    val adapter = CuinaAdapter(llistaComandes)
                    rvComandes.adapter = adapter
                } else {
                    Toast.makeText(this@PantallaCuina, "Error al cargar comandas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PantallaCuina, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Neteja la sessió de l'usuari i el torna a la pantalla de Login.
     */
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