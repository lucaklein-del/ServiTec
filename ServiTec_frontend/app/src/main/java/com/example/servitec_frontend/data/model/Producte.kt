package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Producte(
    @SerializedName("idProducte") val idProducte: Int,
    @SerializedName("nom") val nom: String,
    @SerializedName("preu") val preu: Double,
    @SerializedName("idCategoria") val idCategoria: Int
)
