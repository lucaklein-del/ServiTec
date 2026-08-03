package com.example.servitec_frontend.repository

import com.example.servitec_frontend.data.model.PostProducteDTO
import com.example.servitec_frontend.data.model.Producte

class ProductesRepository {
    private val apiService = RetrofitClient.instance

    suspend fun crearProdcute(nouProducte: PostProducteDTO): Producte? {
        return try {
            val response = apiService.crearProducte(nouProducte)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}