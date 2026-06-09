package com.example.servitec_frontend.data.network

import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.Usuari
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/Usuari/llistar")
    fun getUsuarios(): Call<List<Usuari>>

    @POST("api/Usuari/login")
    fun login(@Body request: LoginRequest): Call<Usuari>

    @GET("api/Categoria/llistar") // Asegúrate de que esta ruta coincide con tu Backend (ej: /api/categories)
    suspend fun getCategories(): Response<List<Categoria>>

    // NUEVO: Obtener todos los productos
    @GET("api/Producte/Llistar") // Asegúrate de que esta ruta coincide con tu Backend (ej: /api/productes)
    suspend fun getProducts(): Response<List<Producte>>

    @POST("api/Comanda/crear") // Ajusta la ruta exacta si en tu C# el controlador no se llama así
    suspend fun crearComanda(@Body dto: CreateComandaDTO): Response<ResponseBody>
}