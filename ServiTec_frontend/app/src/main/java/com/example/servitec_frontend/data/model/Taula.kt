package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Taula(
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("numero") val numero: Int,
    @SerializedName("capacitat") val capacitat: Int,
    @SerializedName("estat") val estat: Boolean,
    @SerializedName("estatComanda") val estatComanda: String? = null
)