package com.example.servitec_frontend.data.model

data class CreateLiniaComandaDTO(
    val postIdProducte: Int,
    val postQuantitat: Int
)

data class CreateComandaDTO(
    val postEstat: String,
    val postIdTaula: Int,
    val postIdUsuari: Int,
    val postLinies: List<CreateLiniaComandaDTO>
)