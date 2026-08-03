package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Producte(
    @SerializedName("idProducte") val idProducte: Int,
    @SerializedName("nom") val nom: String,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("preu") val preu: Double,
    @SerializedName("idCategoria") val idCategoria: Int
){
    val preuTotal: Double
        get() = quantitat * preu
}

data class PostProducteDTO(
    val PostNom: String,
    val PostDescripcio: String,
    val PostPreu: Double,
    val PostActiu: Boolean,
    val PostIdCategoria: Int
)
