package com.example.servitec_frontend.ui.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.ui.PantallaTaula
import kotlin.jvm.java
    class TaulesAdapter(private val llista: List<Taula>) : RecyclerView.Adapter<TaulesAdapter.ViewHolder>() {

        class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
            val tvNumero: TextView = vista.findViewById(R.id.tvNumeroTaula)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.taula, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mesaActual = llista[position]

            holder.tvNumero.text = "T.${mesaActual.numero}"

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, PantallaTaula::class.java)
                intent.putExtra("NUMERO_TAULA", mesaActual.numero)
                it.context.startActivity(intent)
            }
        }
        override fun getItemCount() = llista.size
    }