package com.example.servitec_frontend.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.enableSavedStateHandles
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

    private val historialGuardat = mutableListOf<LiniaComandaTemporal>()
    private val productesSeleccionats = mutableListOf<LiniaComandaTemporal>()

    // Guardem la ID de la comanda si la taula ja està ocupada (Útil per a futures actualitzacions)
    private var idComandaActiva = -1

    private var producteBorrar: LiniaComandaTemporal? = null

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
        val btnBorrar = findViewById<ImageButton>(R.id.btnBorrarProductos)

        mostrarNumeroTaula.text = nTaulaActual

        // 1. Configuración del RecyclerView de Categorías (Izquierda)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        // 2. Configuración del RecyclerView del Centro (Tu comanda actual)
        val rvCentre = findViewById<RecyclerView>(R.id.rvSeleccionProductos)
        rvCentre.layoutManager = LinearLayoutManager(this)
        adapterCentre = ComandaAdapter(emptyList()) { itemPulsat ->
            producteBorrar = itemPulsat
            Toast.makeText(this, "Seleccionat: ${itemPulsat.producte.nom}, ${itemPulsat.idLiniaComanda}", Toast.LENGTH_SHORT).show()
        }
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
                            historialGuardat.add(
                                LiniaComandaTemporal(
                                    idLiniaComanda = linea.idLiniaComanda,
                                    producte = prod,
                                    quantitat = linea.quantitat,
                                    preu = linea.preuUnitari,
                                    total = linea.subtotal,
                                    estat = linea.estat ?: "Enviat"
                                )
                            )
                        }
                    }

                    // Notifiquem al teu adaptador del centre per pintar els productes de la BD
                    adapterCentre.actualitzarLlista(historialGuardat)

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
                    preu = productoPulsado.preu,
                    total = totalInicial,
                    estat = "pendentEnviar"
                ))
            }

            Log.d("DEBUG_CENTRE", "Producte: ${productoPulsado.nom} | Qtd: ${itemExistente?.quantitat ?: 1} | Total Línia: ${itemExistente?.total ?: productoPulsado.preu}€")

            actualitzarTotalInterficie()
        }
        rvProductes.adapter = adapterProductes

        btnEnviar.setOnClickListener {
            // 1. Verificamos que la cesta de la sesión actual no esté vacía
            if (productesSeleccionats.isEmpty()) {
                Toast.makeText(this, "No hi ha cap producte nou per enviar a cuina", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnEnviar.isEnabled = false // Evitamos duplicar peticiones por doble clic

                // 2. Mapeamos ÚNICAMENTE los productos nuevos seleccionados en esta ronda
                val novesLiniesDto = productesSeleccionats.map { l ->
                    CreateLiniaComandaDTO(
                        postIdProducte = l.producte.idProducte,
                        postQuantitat = l.quantitat,
                        postEstat = l.estat // Marcamos como 'Pendent' para que cocina sepa que es nuevo
                    )
                }

                // 3. Consultamos si la mesa ya tiene comanda activa en la BD
                val comandaActiva = repository.obtenirComandaActiva(idTaulaActual)
                val idComandaActiva = comandaActiva?.idComanda ?: -1

                val exit: Boolean

                if (taulaOcupada && idComandaActiva > 0) {
                    // 🔄 MESA OCUPADA: Enviamos SOLO las nuevas líneas a la comanda existente
                    // (Las líneas anteriores ya están guardadas en la BD y NO se reenvían)
                    val resultat = repository.afegirLinies(idComandaActiva, novesLiniesDto)
                    exit = resultat.isSuccess
                } else {
                    // 🆕 MESA LIBRE: Creamos la comanda inicial desde cero
                    val novaComandaDto = CreateComandaDTO(
                        postEstat = "oberta",
                        postIdTaula = idTaulaActual,
                        postIdUsuari = idUsuariActual,
                        postLinies = novesLiniesDto
                    )
                    exit = repository.enviarComanda(novaComandaDto)
                }

                // 4. Gestión del resultado
                if (exit) {
                    Toast.makeText(this@PantallaTaula, "Comanda enviada a cuina correctament!", Toast.LENGTH_LONG).show()

                    // Limpiamos la cesta temporal de la pantalla
                    productesSeleccionats.clear()
                    adapterCentre.actualitzarLlista(productesSeleccionats)
                    actualitzarTotalInterficie()

                    finish() // Volvemos al mapa de mesas
                } else {
                    Toast.makeText(this@PantallaTaula, "Error en connectar amb el servidor", Toast.LENGTH_LONG).show()
                }

                btnEnviar.isEnabled = true
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


            btnBorrar.setOnClickListener {
                val elemento = producteBorrar

                if (elemento != null) {
                    // Només permetem esborrar productes que encara NO s'hagin enviat a cuina
                    if (elemento.idLiniaComanda == 0) {
                        // 1. L'eliminem de la llista de la sessió actual
                        productesSeleccionats.remove(elemento)

                        // 2. ResResetegem la variable del producte seleccionat
                        producteBorrar = null

                        // 3. Treiem el ressaltat blau de l'adapter
                        adapterCentre.netejarSeleccio()

                        // 4. Recalculem el total i refresquem la vista
                        actualitzarTotalInterficie()

                        Toast.makeText(this@PantallaTaula, "Producte eliminat", Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            btnBorrar.isEnabled = false

                            val exit = repository.eliminarLiniaComanda(elemento.idLiniaComanda)

                            if (exit) {
                                if (elemento.quantitat == 1) {
                                    elemento.quantitat -= 1
                                    elemento.estat = "Eliminat"
                                    elemento.total = 0.0
                                }
                                else{
                                    elemento.quantitat -= 1
                                    elemento.total -= elemento.preu
                                }


                                producteBorrar = null
                                adapterCentre.netejarSeleccio()
                                actualitzarTotalInterficie()
                                btnBorrar.isEnabled = true

                                Toast.makeText(
                                    this@PantallaTaula,
                                    "Producte marcat com a eliminat",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@PantallaTaula, "Selecciona un producte de la comanda per esborrar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Funció auxiliar per calcular i refrescar el total a la UI de forma neta
    private fun actualitzarTotalInterficie() {
        val totsElsItems = mutableListOf<LiniaComandaTemporal>()
        totsElsItems.addAll(historialGuardat)       // Lo que ya estaba en la BD
        totsElsItems.addAll(productesSeleccionats)  // Los nuevos (donde ya va el Café con quantitat = 2)

        // Le pasamos la lista lista al adapter central
        adapterCentre.actualitzarLlista(totsElsItems)

        // Actualizamos el total de abajo
        val granTotal = totsElsItems.sumOf { it.total }
        tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
    }
}