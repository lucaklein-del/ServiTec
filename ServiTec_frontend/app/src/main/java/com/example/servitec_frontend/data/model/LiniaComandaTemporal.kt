package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class LiniaComandaTemporal(
    val producte: Producte,
    var quantitat: Int,
    var total: Double,
    var estat: String
)

data class LiniaCuinaDTO(
    @SerializedName("idLiniaComanda") val idLiniaComanda: Int,
    @SerializedName("nomProducte") val nomProducte: String,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("idCategoria") val idCategoria: Int
)