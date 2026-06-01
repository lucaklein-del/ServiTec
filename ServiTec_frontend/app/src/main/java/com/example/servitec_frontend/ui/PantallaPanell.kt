package com.example.servitec_frontend.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.ui.adapter.TaulesAdapter

class PantallaPanell : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)


        // 1. Vincular el RecyclerView del XML con el código
        val rvTaules = findViewById<RecyclerView>(R.id.rvTaules)

        // 2. CONFIGURACIÓN VITAL: Decirle cómo se ordenan (en cuadrícula de 3)
        // Sin esta línea, el RecyclerView no sabe cómo dibujarse y sale vacío
        rvTaules.layoutManager = GridLayoutManager(this, 3)

        // 3. Crear los datos de prueba
        val llistaTaules = listOf(
            Taula(numero = 1),
            Taula(numero = 2),
            Taula(numero = 3)
        )

        // 4. Crear el adapter con la lista y conectarlo
       val adapter = TaulesAdapter(llistaTaules)
        rvTaules.adapter = adapter
    }

}