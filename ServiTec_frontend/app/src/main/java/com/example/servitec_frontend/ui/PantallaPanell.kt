package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.servitec_frontend.R

class PantallaPanell : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)

        // 1. Inicializamos y configuramos cada mesa mapeándola con su ID real de la BD
        configurarMesa(R.id.btnMesa2, idMesaBD = 2, nTaula = "Taula 10")
        configurarMesa(R.id.btnMesa4, idMesaBD = 4, nTaula = "Taula 11")
        configurarMesa(R.id.btnMesa5, idMesaBD = 5, nTaula = "Taula 12")
        configurarMesa(R.id.btnMesa6, idMesaBD = 6, nTaula = "Taula 13")
        configurarMesa(R.id.btnMesa7, idMesaBD = 7, nTaula = "Taula 14")
        configurarMesa(R.id.btnMesa8, idMesaBD = 8, nTaula = "Taula 15")

        // 2. Configuración del botón de cerrar sesión (TextView o Button)
        val btnCerrarSesion = findViewById<View>(R.id.btnCerrarSesion)
        btnCerrarSesion?.setOnClickListener {
            // Finaliza esta actividad y vuelve a la pantalla anterior (Login)
            finish()
        }
    }

    /**
     * Función genérica para vincular los botones del XML con la lógica de negocio de ServiTec.
     * @param resId El ID del componente en el archivo XML (ej: R.id.btnMesa2)
     * @param idMesaBD El ID único que tiene esta mesa en tu base de datos (ASP.NET Core)
     * @param nombreMesa El nombre amigable para mostrar en pantalla
     */
    private fun configurarMesa(resId: Int, idMesaBD: Int, nTaula: String) {
        val botonMesa = findViewById<AppCompatButton>(resId)

        botonMesa?.setOnClickListener {
            // Por ahora mostramos un aviso en pantalla para verificar que la ID es correcta
            Toast.makeText(
                this,
                "Abriendo $nTaula (ID BD: $idMesaBD)",
                Toast.LENGTH_SHORT
            ).show()


            val intent = Intent(this, PantallaTaula::class.java).apply {
                putExtra("idTaula", idMesaBD)
                putExtra("nTaula", nTaula)
            }
            startActivity(intent)

        }
    }
}