package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.repository.MesaRepository
import com.example.servitec_frontend.ui.adapter.CategoriesAdapter
import com.example.servitec_frontend.ui.adapter.ProductesAdapter
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class PantallaTaula : AppCompatActivity() {

    private lateinit var adapterProductes: ProductesAdapter
    private var totsElsProductes = listOf<Producte>()
    private val repository = MesaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_taula)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        val rvProductes = findViewById<RecyclerView>(R.id.rvPedido)
        rvProductes.layoutManager = GridLayoutManager(this, 2)

        adapterProductes = ProductesAdapter(emptyList<Producte>())
        rvProductes.adapter = adapterProductes

        lifecycleScope.launch {

            val categoriesBD = repository.obtenerCategorias()
            val productesBD = repository.obtenerProductos() ?: emptyList()
            totsElsProductes = productesBD

            val adapterCategories = CategoriesAdapter(categoriesBD ?: emptyList()) { categoriaPulsada ->
                println("DEBUG_FILTRO: Pulsada categoría ID -> ${categoriaPulsada.idCategoria}")
                val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria}
                adapterProductes.actualitzarLlista(productesFiltrats)
                println("DEBUG_FILTRO: Productos filtrados encontrados -> ${productesFiltrats.size}")
            }

            rvCategories.adapter = adapterCategories
            if (categoriesBD != null && productesBD != null) {
                totsElsProductes = productesBD
                val adapterCategories = CategoriesAdapter(categoriesBD) { categoriaPulsada ->
                    val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria}
                    println("DEBUG_FILTRO: Productos filtrados encontrados -> ${productesFiltrats.size}")
                    adapterProductes.actualitzarLlista(productesFiltrats)
                }
                rvCategories.adapter = adapterCategories

            } else {
                Toast.makeText(this@PantallaTaula, "Error al cargar los datos del servidor", Toast.LENGTH_LONG).show()
            }
        }
    }
}