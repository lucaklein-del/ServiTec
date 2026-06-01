package com.example.servitec_frontend.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria

class CategoriesAdapter(
    private val llista: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private var posicionSeleccionada = 0

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val textview: TextView = vista.findViewById(R.id.tvNombreCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = llista[position]
        holder.textview.text = cat.nom

        if (position == posicionSeleccionada) {
            holder.textview.setBackgroundResource(R.drawable.bg_button_selected)
            holder.textview.setTextColor(Color.WHITE)
        } else {
            holder.textview.setBackgroundColor(Color.TRANSPARENT)
            holder.textview.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            posicionSeleccionada = position
            notifyDataSetChanged()
            onCategoriaClick(cat)
        }
    }

    override fun getItemCount() = llista.size
}