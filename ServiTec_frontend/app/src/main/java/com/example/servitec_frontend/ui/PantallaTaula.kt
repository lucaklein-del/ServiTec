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
import com.example.servitec_frontend.repository.TaulaRepository
import com.example.servitec_frontend.ui.adapter.CategoriesAdapter
import com.example.servitec_frontend.ui.adapter.ComandaAdapter
import com.example.servitec_frontend.ui.adapter.ProductesAdapter
import kotlinx.coroutines.launch

class PantallaTaula : AppCompatActivity() {

    private lateinit var adapterProductes: ProductesAdapter
    private lateinit var adapterCentre: ComandaAdapter
    private lateinit var tvTotalPreu: TextView
    private lateinit var btnEnviar : Button
    private lateinit var  btnCobrar : Button
    private lateinit var bntSorir : Button
    private lateinit var mostrarNumeroTaula: TextView

    private var totsElsProductes = listOf<Producte>()
    private val repository = TaulaRepository()

    // El "carrito" local en memoria
    private val productesSeleccionats = mutableListOf<LiniaComandaTemporal>()

    // Guardem la ID de la comanda si la taula ja està ocupada (Útil per a futures actualitzacions)
    private var idComandaActiva = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_taula)

        tvTotalPreu = findViewById(R.id.tvTotalPrecio)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnCobrar = findViewById(R.id.btnCobrar)
        bntSorir = findViewById(R.id.btnVolver)
        mostrarNumeroTaula = findViewById(R.id.tvTituloMesa)

        val idTaulaActual = intent.getIntExtra("idTaula", -1)
        val nTaulaActual = intent.getStringExtra("nTaula") ?: "Taula"
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        val idUsuariActual = sharedPreferences.getInt("idUsuari", -1)
        val taulaOcupada = intent.getBooleanExtra("taulaOcupada", false)

        mostrarNumeroTaula.text = nTaulaActual

        // 1. Configuración del RecyclerView de Categorías (Izquierda)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        // 2. Configuración del RecyclerView del Centro (Tu comanda actual)
        val rvCentre = findViewById<RecyclerView>(R.id.rvSeleccionProductos)
        rvCentre.layoutManager = LinearLayoutManager(this)
        adapterCentre = ComandaAdapter(productesSeleccionats)
        rvCentre.adapter = adapterCentre

        // 3. Configuración del RecyclerView de Productos (Derecha / Cuadrícula)
        val rvProductes = findViewById<RecyclerView>(R.id.rvPedido)
        rvProductes.layoutManager = GridLayoutManager(this, 2)

        // 🎇 RECUPERACIÓ DE LA COMANDA ACTIVA SI LA TAULA ESTÀ OCUPADA
        if (taulaOcupada && idTaulaActual != -1) {
            lifecycleScope.launch {
                val comandaActiva = repository.obtenirComandaActiva(idTaulaActual)

                if (comandaActiva != null) {
                    idComandaActiva = comandaActiva.idComanda

                    // Netegem el carrito local abans de carregar les dades de SQL Server
                    productesSeleccionats.clear()

                    comandaActiva.liniaComanda?.forEach { linea ->
                        val prod = linea.idProducteNavigation
                        if (prod != null) {
                            productesSeleccionats.add(
                                LiniaComandaTemporal(
                                    producte = prod,
                                    quantitat = linea.quantitat,
                                    total = linea.subtotal
                                )
                            )
                        }
                    }

                    // Notifiquem al teu adaptador del centre per pintar els productes de la BD
                    adapterCentre.actualitzarLlista(productesSeleccionats)

                    // Actualitzem el TextView amb el preu total que ve del Back-end
                    tvTotalPreu.text = "${String.format("%.2f", comandaActiva.total)}€"
                }
            }
        }

        // Lógica del clic en los productos de la cuadrícula
        adapterProductes = ProductesAdapter(emptyList()) { productoPulsado ->
            val itemExistente = productesSeleccionats.find { it.producte.idProducte == productoPulsado.idProducte }

            if (itemExistente != null) {
                itemExistente.quantitat++
                itemExistente.total = itemExistente.producte.preu * itemExistente.quantitat
            } else {
                val totalInicial = productoPulsado.preu * 1
                productesSeleccionats.add(LiniaComandaTemporal(
                    producte = productoPulsado,
                    quantitat = 1,
                    total = totalInicial
                ))
            }

            Log.d("DEBUG_CENTRE", "Producte: ${productoPulsado.nom} | Qtd: ${itemExistente?.quantitat ?: 1} | Total Línia: ${itemExistente?.total ?: productoPulsado.preu}€")

            adapterCentre.actualitzarLlista(productesSeleccionats)
            actualitzarTotalInterficie()
        }
        rvProductes.adapter = adapterProductes

        btnEnviar.setOnClickListener {
            if (productesSeleccionats.isEmpty()) {
                Toast.makeText(this, "No pots enviar una comanda buida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val liniesDto = productesSeleccionats.map { l ->
                CreateLiniaComandaDTO(
                    postIdProducte = l.producte.idProducte,
                    postQuantitat = l.quantitat
                )
            }

            val novaComandaDto = CreateComandaDTO(
                postEstat = "oberta",
                postIdTaula = idTaulaActual,
                postIdUsuari = idUsuariActual,
                postLinies = liniesDto
            )

            lifecycleScope.launch {
                btnEnviar.isEnabled = false // Evitem doble clic erroni

                val exit = repository.enviarComanda(novaComandaDto)

                if (exit) {
                    Toast.makeText(this@PantallaTaula, "Comanda enviada a cuina correctament!", Toast.LENGTH_LONG).show()
                    productesSeleccionats.clear()
                    adapterCentre.actualitzarLlista(productesSeleccionats)
                    actualitzarTotalInterficie()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error al conectar amb el servidor", Toast.LENGTH_LONG).show()
                }

                btnEnviar.isEnabled = true
                finish()
            }
        }

        btnCobrar.setOnClickListener {
            if (idComandaActiva == -1) {
                Toast.makeText(this, "No hi ha cap comanda activa per cobrar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnCobrar.isEnabled = false
                val cobrado = repository.cobrarComanda(idComandaActiva)

                if (cobrado) {
                    Toast.makeText(this@PantallaTaula, "Mesa cobrada correctament!", Toast.LENGTH_LONG).show()

                    // Limpiamos los productos locales de la pantalla
                    productesSeleccionats.clear()
                    adapterCentre.actualitzarLlista(productesSeleccionats)
                    actualitzarTotalInterficie()

                    // Cerramos la pantalla para volver al panel general
                    finish()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error al cobrar la comanda", Toast.LENGTH_SHORT).show()
                }
                btnCobrar.isEnabled = true
            }
        }

        bntSorir.setOnClickListener{
            finish()
        }

        // 4. Carga inicial de categorías y productos desde el Repositorio
        lifecycleScope.launch {
            val categoriesBD = repository.obtenirCategories()
            val productesBD = repository.obtenerProductos() ?: emptyList()
            totsElsProductes = productesBD

            if (categoriesBD != null) {
                val adapterCategories = CategoriesAdapter(categoriesBD) { categoriaPulsada ->
                    val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria }
                    adapterProductes.actualitzarLlista(productesFiltrats)
                }
                rvCategories.adapter = adapterCategories

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

    // Funció auxiliar per calcular i refrescar el total a la UI de forma neta
    private fun actualitzarTotalInterficie() {
        val granTotal = productesSeleccionats.sumOf { it.total }
        tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
    }
}