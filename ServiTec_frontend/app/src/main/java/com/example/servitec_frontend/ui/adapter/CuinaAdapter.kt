package com.example.servitec_frontend.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.ResponseCuina
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuinaAdapter(
    private val comandes: MutableList<ResponseCuina>
) : RecyclerView.Adapter<CuinaAdapter.CuinaViewHolder>() {

    class CuinaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumTaula: TextView = view.findViewById(R.id.tvNumTaula)
        val tvHoraComanda: TextView = view.findViewById(R.id.tvHoraComanda)
        val containerBebidas: LinearLayout = view.findViewById(R.id.containerBebidas)
        val containerPrimeros: LinearLayout = view.findViewById(R.id.containerPrimeros)
        val containerSegundos: LinearLayout = view.findViewById(R.id.containerSegundos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comanda_cuina, parent, false)
        return CuinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuinaViewHolder, position: Int) {
        val comanda = comandes[position]

        // 1. Mostrar nombre de la mesa
        holder.tvNumTaula.text = "Taula ${comanda.numTaula}"

        // 2. Extraer y formatear la hora (de "2026-07-23T14:30:00" saca "14:30")
        holder.tvHoraComanda.text = if (comanda.dataHora?.contains("T") == true) {
            comanda.dataHora.substringAfter("T").take(5)
        } else {
            comanda.dataHora ?: ""
        }

        // 3. Limpiar los contenedores por si el ViewHolder se reutiliza
        holder.containerBebidas.removeAllViews()
        holder.containerPrimeros.removeAllViews()
        holder.containerSegundos.removeAllViews()

        // Contador para llevar el control local de platos activos en este ticket
        var platsPendentsInTicket = comanda.linies.size

        // 4. Recorrer las líneas (que el backend ya devuelve filtradas por Estat == "Pendent")
        for (linia in comanda.linies) {
            val nombreValido = linia.nomProducte ?: "Producte sense nom"

            val tvPlato = TextView(holder.itemView.context).apply {
                text = "${linia.quantitat}x  $nombreValido"
                textSize = 14f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(0, 8, 0, 8)
            }

            tvPlato.setOnClickListener {
                val idLinia = linia.idLiniaComanda ?: return@setOnClickListener

                // Ocultar plato visualmente
                tvPlato.visibility = View.GONE
                platsPendentsInTicket--

                // Si era el último plato de la comanda, quitamos la comanda entera
                if (platsPendentsInTicket <= 0) {
                    val posActual = holder.bindingAdapterPosition
                    if (posActual != RecyclerView.NO_POSITION && posActual in comandes.indices) {
                        comandes.removeAt(posActual)
                        notifyItemRemoved(posActual)
                        notifyItemRangeChanged(posActual, comandes.size)
                    }
                }

                // Avisar a la API para marcar la línea como "Servit" en BD
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.instance.canviarEstatLinia(idLinia, "Servit")
                        if (!response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                tvPlato.visibility = View.VISIBLE
                                Toast.makeText(holder.itemView.context, "Error en actualitzar l'estat", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            tvPlato.visibility = View.VISIBLE
                            Toast.makeText(holder.itemView.context, "Error de connexió", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Clasificación por categoría
            when (linia.idCategoria) {
                1 -> holder.containerBebidas.addView(tvPlato)
                2 -> holder.containerPrimeros.addView(tvPlato)
                3 -> holder.containerSegundos.addView(tvPlato)
                else -> holder.containerPrimeros.addView(tvPlato)
            }
        }
    }

    override fun getItemCount(): Int = comandes.size
}