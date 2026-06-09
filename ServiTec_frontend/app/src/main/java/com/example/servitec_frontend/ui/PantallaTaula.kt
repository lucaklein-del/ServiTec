package com.example.servitec_frontend.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.LiniaComandaTemporal
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.repository.MesaRepository
import com.example.servitec_frontend.ui.adapter.CategoriesAdapter
import com.example.servitec_frontend.ui.adapter.ComandaAdapter
import com.example.servitec_frontend.ui.adapter.ProductesAdapter
import kotlinx.coroutines.launch

class PantallaTaula : AppCompatActivity() {

    private lateinit var adapterProductes: ProductesAdapter
    private lateinit var adapterCentre: ComandaAdapter
    private lateinit var tvTotalPreu: TextView
    private lateinit var btnEnviar : Button
    private lateinit var bntSorir : Button

    private var totsElsProductes = listOf<Producte>()
    private val repository = MesaRepository()

    // El "carrito" local en memoria
    private val productesSeleccionats = mutableListOf<LiniaComandaTemporal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_taula)

        tvTotalPreu = findViewById(R.id.tvTotalPrecio)
        btnEnviar = findViewById(R.id.btnEnviar)
        bntSorir = findViewById(R.id.btnVolver)

        // 1. Configuración del RecyclerView de Categorías (Izquierda)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        // 2. Configuración del RecyclerView del Centro (Tu comanda actual)
        val rvCentre = findViewById<RecyclerView>(R.id.rvSeleccionProductos) // 🔥 Inicializado aquí
        rvCentre.layoutManager = LinearLayoutManager(this)
        adapterCentre = ComandaAdapter(productesSeleccionats)
        rvCentre.adapter = adapterCentre

        // 3. Configuración del RecyclerView de Productos (Derecha / Cuadrícula)
        val rvProductes = findViewById<RecyclerView>(R.id.rvPedido)
        rvProductes.layoutManager = GridLayoutManager(this, 2)


        // Lógica del clic en los productos de la cuadrícula
        adapterProductes = ProductesAdapter(emptyList()) { productoPulsado ->
            // 1. Buscamos si el producto ya está en la lista del centro
            val itemExistente = productesSeleccionats.find { it.producte.idProducte == productoPulsado.idProducte }

            if (itemExistente != null) {
                // 2. Si ya existe: sumamos 1 a la cantidad...
                itemExistente.quantitat++
                // ...y recalculamos su campo total con la nueva cantidad
                itemExistente.total = itemExistente.producte.preu * itemExistente.quantitat
            } else {
                // 3. Si es nuevo: calculamos el total inicial (precio * 1)
                val totalInicial = productoPulsado.preu * 1

                productesSeleccionats.add(LiniaComandaTemporal(
                    producte = productoPulsado,
                    quantitat = 1,
                    total = totalInicial
                ))
            }

            Log.d("DEBUG_CENTRE", "Producte: ${productoPulsado.nom} | Qtd: ${itemExistente?.quantitat ?: 1} | Total Línia: ${itemExistente?.total ?: productoPulsado.preu}€")

            // 4. Refrescamos visualmente el RecyclerView del centro
            adapterCentre.actualitzarLlista(productesSeleccionats)

            val granTotal = productesSeleccionats.sumOf { it.total }
            tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
        }
        rvProductes.adapter = adapterProductes

        btnEnviar.setOnClickListener {
            if (productesSeleccionats.isEmpty()) {
                Toast.makeText(this, "No pots enviar una comanda buida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mapeamos la lista temporal del centro al formato de sub-DTOs que entiende C#
            val liniesDto = productesSeleccionats.map { l ->
                CreateLiniaComandaDTO(
                    postIdProducte = l.producte.idProducte,
                    postQuantitat = l.quantitat
                )
            }

            // Construimos el DTO principal.
            // NOTA: De momento pongo IDs fijos (Taula 1, Usuari 1). Cámbialos por los reales de tu sesión si los tienes.
            val novaComandaDto = CreateComandaDTO(
                postEstat = "oberta",
                postIdTaula = 2,
                postIdUsuari = 1,
                postLinies = liniesDto
            )

            fun actualitzarTotalInterficie() {
                val granTotal = productesSeleccionats.sumOf { it.total }
                tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
            }

            // Enviamos los datos al servidor de forma asíncrona usando la corrutina
            lifecycleScope.launch {
                btnEnviar.isEnabled = false // Desactivamos el botón temporalmente para evitar doble clic

                val exit = repository.enviarComanda(novaComandaDto)

                if (exit) {
                    Toast.makeText(this@PantallaTaula, "Comanda enviada a cuina correctament!", Toast.LENGTH_LONG).show()

                    // ¡Éxito! Vaciamos el carrito local y refrescamos la interfaz
                    adapterCentre.actualitzarLlista(productesSeleccionats)
                    actualitzarTotalInterficie()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error al conectar amb el servidor", Toast.LENGTH_LONG).show()
                }

                btnEnviar.isEnabled = true // Volvemos a activar el botón
            }
        }

        bntSorir.setOnClickListener{
            finish()
        }

        // 4. Carga de datos desde tu Repositorio
        lifecycleScope.launch {
            val categoriesBD = repository.obtenerCategorias()
            val productesBD = repository.obtenerProductos() ?: emptyList()
            totsElsProductes = productesBD

            if (categoriesBD != null) {
                val adapterCategories = CategoriesAdapter(categoriesBD) { categoriaPulsada ->
                    println("DEBUG_FILTRO: Pulsada categoría ID -> ${categoriaPulsada.idCategoria}")
                    val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria }
                    adapterProductes.actualitzarLlista(productesFiltrats)
                }
                rvCategories.adapter = adapterCategories

                // Carga inicial automática de la primera categoría
                if (categoriesBD.isNotEmpty()) {
                    val primeraCatId = categoriesBD[0].idCategoria
                    val productesInicials = totsElsProductes.filter { it.idCategoria == primeraCatId }
                    adapterProductes.actualitzarLlista(productesInicials)
                }


            } else {
                Toast.makeText(this@PantallaTaula, "Error al cargar los datos del servidor", Toast.LENGTH_LONG).show()
            }
        }
    }
}