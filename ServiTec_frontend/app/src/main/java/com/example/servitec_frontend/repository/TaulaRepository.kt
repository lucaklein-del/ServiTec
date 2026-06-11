package com.example.servitec_frontend.repository

import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.Taula
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class taulaRepository {

    private val apiService = RetrofitClient.instance
    suspend fun obtenerCategorias(): List<Categoria>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }
    suspend fun obtenerProductos(): List<Producte>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun enviarComanda(dto: CreateComandaDTO): Boolean {
        return try {
            // Asumo que tienes una instancia de la API llamada 'apiService' o similar dentro del repositorio
            // Reemplaza 'apiService' por el nombre de la variable que uses para llamar a Retrofit
            val response = apiService.crearComanda(dto)

            // Si el servidor devuelve un código de éxito (como el 201 Created que pusiste en C#)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false // Si hay un error de red, devolvemos un 'false' para controlarlo en la interfaz
        }
    }

    suspend fun obtenirTaules(): List<Taula>? {
        return try {
            val response = apiService.obtenirTaules() // Ajusta a cómo se llame tu instancia de API
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}