package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Taula(
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("numero") val numero: Int,
    @SerializedName("capacitat") val capacitat: Int,
    @SerializedName("estat") val estat: Boolean,
    @SerializedName("estatComanda") val estatComanda: String? = null,
    @SerializedName("posX") val posX: Float,
    @SerializedName("posY") val posY: Float
)


data class PostTaulaDTO(
    @SerializedName("Numero") val numero: Int,
    @SerializedName("Capacitat") val capacitat: Int,
    @SerializedName("Estat") val estat: Boolean,
    @SerializedName("IdMenjador") val idMenjador: Int,
    @SerializedName("PosX") val posX: Float,
    @SerializedName("PosY") val posY: Float
)