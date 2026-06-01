package com.example.servitec_frontend.ui

import UserRepository
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servitec_frontend.R

class PantallaLogin : AppCompatActivity() {
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser = findViewById<EditText>(R.id.idUsuari)
        val etPass = findViewById<EditText>(R.id.idContrasenya)

        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            userRepository.loginUser(user, pass) { usuario, error ->
                if (usuario != null) {
                    Toast.makeText(this, "Hola, ${usuario.nomUsuari}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PantallaPanell::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, error ?: "Error de conexión", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}