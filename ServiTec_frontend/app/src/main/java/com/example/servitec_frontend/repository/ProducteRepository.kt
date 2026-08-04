package com.example.servitec_frontend.repository

import com.example.servitec_frontend.data.model.PostProducteDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.PutProducteDTO
import com.example.servitec_frontend.data.model.PutUsuariDTO

class ProducteRepository {
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

    suspend fun llistarProductes(): List<ProducteDTO>?{
        return try{
            val response = apiService.obtenirProductes()
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

    suspend fun eliminarProdcute(idProducte: Int): Boolean{
        return try {
            val response = apiService.eliminarProducte(idProducte)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun actualitzarProducte(idProducte: Int, producte: PutProducteDTO): Boolean {
        return try {
            val response = apiService.actualitzarProducte(idProducte, producte)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}