package com.example.servitec_frontend.data.model
import com.google.gson.annotations.SerializedName

data class UsuariDTO(
    val idUsuari: Int,
    val nomUsuari: String,
    val contrasenya: String,
    val actiu: Boolean,
    val admin: Boolean,
    val rol: String
)

data class PutUsuariDTO(
    val PutNomUsuari : String,
    val PutContrasenya : String,
    val PutActiu : Boolean,
    val PutAdmin : Boolean,
    val PutRol : String
)

data class CrearUsuariDTO(
    @SerializedName("PostNomUsuari") val postNomUsuari: String,
    @SerializedName("PostContrasenya") val postContrasenya: String,
    @SerializedName("PostActiu") val postActiu: Boolean,
    @SerializedName("PostAdmin") val postAdmin: Boolean,
    @SerializedName("PostRol") val postRol: String
)