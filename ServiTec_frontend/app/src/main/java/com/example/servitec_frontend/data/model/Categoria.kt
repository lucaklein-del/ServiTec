package com.example.servitec_frontend.data.model

data class Categoria(
    val idCategoria: Int,
    val nom: String,
    val descripcio: String?
)

data class PutCategoriaDTO(
    val putIdCategoria: Int,
    val putNom: String,
    val putDescripcio: String?
)