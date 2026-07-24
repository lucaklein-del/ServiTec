package com.example.servitec_frontend.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.ResponseCuina

class CuinaAdapter(
    private val comandes: List<ResponseCuina>
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
        holder.tvNumTaula.text = comanda.numTaula ?: "Mesa ${comanda.numTaula}"

        // 2. Extraer y formatear la hora (de "2026-07-23T14:30:00" saca "14:30")
        holder.tvHoraComanda.text = if (comanda.dataHora.contains("T")) {
            comanda.dataHora.substringAfter("T").take(5)
        } else {
            comanda.dataHora
        }

        // 3. Limpiar los contenedores por si el ViewHolder se reutiliza
        holder.containerBebidas.removeAllViews()
        holder.containerPrimeros.removeAllViews()
        holder.containerSegundos.removeAllViews()

        // 4. Recorrer las líneas de la comanda y añadirlas a su sección según idCategoria
        for (linia in comanda.linies) {
            val tvPlato = TextView(holder.itemView.context).apply {
                text = "${linia.quantitat}x  ${linia.nomProducte}"
                textSize = 14f
                setTextColor(android.graphics.Color.BLACK)
                setPadding(0, 4, 0, 4)
            }

            // Cambia estos IDs si en tu base de datos las categorías son distintas:
            // Ejemplo: 1 = Bebidas, 2 = Primeros, 3 = Segundos
            when (linia.idCategoria) {
                1 -> holder.containerBebidas.addView(tvPlato)
                2 -> holder.containerPrimeros.addView(tvPlato)
                3 -> holder.containerSegundos.addView(tvPlato)
                else -> holder.containerPrimeros.addView(tvPlato) // Categoría por defecto
            }
        }
    }

    override fun getItemCount(): Int = comandes.size
}