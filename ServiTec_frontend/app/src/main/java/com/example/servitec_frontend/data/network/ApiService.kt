package com.example.servitec_frontend.data.network

import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ComandaDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ResponseComnada
import com.example.servitec_frontend.data.model.ResponseCuina
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.data.model.Usuari
import com.example.servitec_frontend.ui.adapter.TaulesAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("api/Taula/llistar")
    suspend fun obtenirTaules(): Response<List<Taula>>

    @GET("api/Comanda/activa/{id}")
    suspend fun obtenirComandaActiva(@Path("id") idMesa: Int): Response<ResponseComnada>

    @GET("api/comanda/cuina")
    suspend fun getComandesCuina(): Response<MutableList<ResponseCuina>>

    @PUT("api/comanda/{id}/estat")
    suspend fun canviarEstatComanda(@Path("id") idComanda: Int, @Body nouEstat: String): Response<ResponseBody>

    @PUT("api/comanda/linia/{idLinia}/estat")
    suspend fun canviarEstatLinia(@Path("idLinia") idLinia: Int, @Body nouEstat: String): Response<ResponseBody>

    @PUT("api/comanda/{idComanda}/cobrar")
    suspend fun cobrarComanda(@Path("idComanda") idComanda: Int): Response<ResponseBody>

    // ApiService.kt
    @POST("api/Comanda/{id}/linies")
    suspend fun afegirLinies(@Path("id") idComanda: Int, @Body linies: List<CreateLiniaComandaDTO>): Response<ComandaDTO>
}

