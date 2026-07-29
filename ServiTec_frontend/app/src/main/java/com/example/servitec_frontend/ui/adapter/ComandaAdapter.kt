package com.example.servitec_frontend.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.LiniaComandaTemporal

class ComandaAdapter(
    private var llista: List<LiniaComandaTemporal>,
    private val onItemClick: (LiniaComandaTemporal) -> Unit
) : RecyclerView.Adapter<ComandaAdapter.ViewHolder>() {

    // 💡 Variable per guardar l'índex de l'element seleccionat (-1 = cap)
    private var posicioSeleccionada: Int = RecyclerView.NO_POSITION

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvQuantitat: TextView = vista.findViewById(R.id.tvQuantitatCentre)
        val tvNom: TextView = vista.findViewById(R.id.tvNomCentre)
        val tvPreu: TextView = vista.findViewById(R.id.tvPreuCentre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comanda, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = llista[position]

        holder.tvQuantitat.text = "${item.quantitat}x"
        holder.tvNom.text = item.producte.nom

        val preuTotal = item.producte.preu * item.quantitat
        holder.tvPreu.text = "${String.format("%.2f", preuTotal)}€"

        if (item.estat == "Eliminat") {
            // 1. Si está eliminado -> Fondo rojo siempre
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red_deleted)
            )
        } else {
            // 2. Si NO está eliminado, comprobamos si está seleccionado
            if (position == posicioSeleccionada) {
                holder.itemView.setBackgroundColor(Color.parseColor("#DBEAFE")) // Azul
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT) // Normal
            }
        }

        // 💡 Clic de selecció actualitzat sense métodes 'deprecated'
        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition

            // Ens assegurem que la posició sigui vàlida a la vista
            if (currentPosition != RecyclerView.NO_POSITION) {
                val posicioAnterior = posicioSeleccionada
                posicioSeleccionada = currentPosition

                // Notifiquem els canvis per refrescar només les 2 files afectades
                notifyItemChanged(posicioAnterior)
                notifyItemChanged(posicioSeleccionada)

                onItemClick(item)
            }
        }
    }

    override fun getItemCount() = llista.size

    fun actualitzarLlista(novaLlista: List<LiniaComandaTemporal>) {
        this.llista = novaLlista.toList()
        // Resresetegem la selecció quan la llista canvia o s'esborra un ítem
        posicioSeleccionada = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    fun netejarSeleccio() {
        val posAnterior = posicioSeleccionada
        posicioSeleccionada = RecyclerView.NO_POSITION
        if (posAnterior != RecyclerView.NO_POSITION) {
            notifyItemChanged(posAnterior)
        }
    }
}