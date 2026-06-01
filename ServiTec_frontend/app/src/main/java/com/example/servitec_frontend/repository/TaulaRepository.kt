package com.example.servitec_frontend.repository

import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.Producte
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MesaRepository {

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
}