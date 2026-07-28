package com.example.servitec_frontend.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.LiniaComandaTemporal

class ComandaAdapter(
    private var llista: List<LiniaComandaTemporal>
) : RecyclerView.Adapter<ComandaAdapter.ViewHolder>() {

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

        // Calculamos el precio total de esa línea (precio producto * cantidad)
        val preuTotal = item.producte.preu * item.quantitat
        holder.tvPreu.text = "${preuTotal}€"
    }

    override fun getItemCount() = llista.size

    // Función para refrescar el centro cada vez que añadimos un producto
    fun actualitzarLlista(novaLlista: List<LiniaComandaTemporal>) {
        this.llista = novaLlista.toList()
        notifyDataSetChanged()
    }
}