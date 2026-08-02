package com.example.servitec_frontend.repository
import com.example.servitec_frontend.data.model.CrearUsuariDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.UsuariDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsuariRepository {
    private val apiService = RetrofitClient.instance

    fun loginUser(user: String, pass: String, onResult: (UsuariDTO?, String?) -> Unit) {
        val loginData = LoginRequest(user, pass)

        apiService.login(loginData).enqueue(object : Callback<UsuariDTO> {
            override fun onResponse(call: Call<UsuariDTO>, response: Response<UsuariDTO>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Error: Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<UsuariDTO>, t: Throwable) {
                onResult(null, "Error de red: ${t.message}")
            }
        })
    }


    suspend fun crearUsuari(nouUsuari: CrearUsuariDTO): UsuariDTO? {
        return try {
            val response = apiService.crearUsuari(nouUsuari)
            if (response.isSuccessful) {
                response.body() // Devuelve el usuario creado si el servidor responde 200/201
            } else {
                null // Error de validación o del servidor
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}