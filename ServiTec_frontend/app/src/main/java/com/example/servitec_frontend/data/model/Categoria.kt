package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    val idCategoria: Int,
    val nom: String,
    val descripcio: String?
)

data class PutCategoriaDTO(
    @SerializedName("PutNom") val putNom: String,
    @SerializedName("PutDescripcio") val putDescripcio: String?
)

data class PostCategoriaDTO(
    @SerializedName("PostNom") val postNom: String,
    @SerializedName("PostDescripcio")val postDescripcio: String?
)