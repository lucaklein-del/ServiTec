/**
 * @file        PantallaLogin.kt
 * @project     ServiTec Frontend
 * @package     com.example.servitec_frontend.ui
 *
 * @description Activity que gestiona la pantalla d'inici de sessió de l'aplicació ServiTec.
 *              Permet a l'usuari introduir les seves credencials i autenticar-se contra
 *              el servidor mitjançant el repositori d'usuaris.
 *
 * @author      [Nom Cognom]
 * @version     1.0.0
 * @since       2024-01-01
 *
 * @see         UserRepository
 * @see         PantallaPanell
 */

package com.example.servitec_frontend.ui

import UserRepository
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.servitec_frontend.R
import androidx.core.content.edit
import kotlin.jvm.java

/**
 * Activity principal d'autenticació de l'aplicació ServiTec.
 *
 * Gestiona el flux d'inici de sessió de l'usuari: recull les credencials
 * introduïdes al formulari, les valida a través de [UserRepository.loginUser]
 * i redirigeix a [PantallaPanell] en cas d'èxit, o mostra un missatge d'error
 * en cas de fallada.
 *
 * ## Flux principal
 * 1. L'usuari introdueix nom d'usuari i contrasenya.
 * 2. Es crida [UserRepository.loginUser] de forma asíncrona.
 * 3. En cas d'èxit → es navega a [PantallaPanell] i es destrueix aquesta Activity.
 * 4. En cas d'error → es mostra un [Toast] amb el missatge corresponent.
 *
 * ## Precondicions
 * - El layout `R.layout.pantalla_login` ha d'existir i contenir els elements
 *   `R.id.btnLogin`, `R.id.idUsuari` i `R.id.idContrasenya`.
 * - El servidor backend ha d'estar accessible des del dispositiu.
 * - [UserRepository] ha d'estar correctament inicialitzat.
 *
 * ## Postcondicions
 * - En cas d'autenticació correcta, l'Activity es destrueix ([finish]) i
 *   s'inicia [PantallaPanell].
 * - En cas de credencials incorrectes o error de xarxa, l'Activity roman activa
 *   i es mostra un [Toast] descriptiu a l'usuari.
 *
 * @constructor Crea una instància de [PantallaLogin] i inicialitza [UserRepository].
 *
 * @throws RuntimeException si els elements del layout no es troben al fitxer XML.
 *
 * @sample
 * ```
 * // Aquesta Activity s'inicia normalment des del Launcher o des de qualsevol
 * // punt de l'app que requereixi autenticació:
 * val intent = Intent(context, PantallaLogin::class.java)
 * startActivity(intent)
 * ```
 */
class PantallaLogin : AppCompatActivity() {

    /** Repositori encarregat de la comunicació amb l'API d'usuaris. */
    private val userRepository = UserRepository()

    /**
     * Mètode del cicle de vida d'Android. Inicialitza la UI i configura
     * els listeners dels elements interactius.
     *
     * @param savedInstanceState Bundle que conté l'estat prèviament desat de l'Activity,
     *                           o `null` si és la primera vegada que es crea.
     *
     * @pre  El fitxer de layout `R.layout.pantalla_login` ha d'estar correctament definit.
     * @post La UI queda inicialitzada i el listener del botó de login està actiu.
     */
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser  = findViewById<EditText>(R.id.idUsuari)
        val etPass  = findViewById<EditText>(R.id.idContrasenya)
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)


        /**
         * Listener del botó d'inici de sessió.
         *
         * Recull les credencials dels camps de text i invoca [UserRepository.loginUser].
         * La crida és asíncrona; el resultat s'avalua al callback rebut.
         *
         * @pre  Els camps [etUser] i [etPass] poden contenir qualsevol cadena de text,
         *       incloent-hi cadenes buides.
         * @post Si l'autenticació té èxit, s'inicia [PantallaPanell] i es destrueix
         *       l'Activity actual. En cas contrari, es mostra un [Toast] d'error.
         */
        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            userRepository.loginUser(user, pass) { usuario, error ->
                if (usuario != null) {
                    Toast.makeText(
                        this,
                        "Hola, ${usuario.nomUsuari}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 1. Desem l'ID de l'usuari i el seu rol a les SharedPreferences
                    sharedPreferences.edit {
                        putInt("idUsuari", usuario.idUsuari)
                        // Mantenim registre del rol si es necessita a posteriori
                        putString("rolUsuari", usuario.rol.toString())
                    }

                    // 2. Comprovem el Rol per definir la destinació
                    // Accepta tant el número INT (1=Camarero, 2=Cocinero) com el nom del ROL (Enum/String)
                    val intent = when (usuario.rol.toString().lowercase()) {
                        "cocinero", "cuiner" -> {
                            // 🍳 Enruta a la pantalla de Cocina
                            Intent(this, PantallaCuina::class.java)
                        }
                        "1", "camarero", "cabrer", "administrador" -> {
                            // ☕ Enruta al mapa de mesas / panel principal
                            Intent(this, PantallaPanell::class.java)
                        }
                        else -> {
                            // Per defecte, si no reconeix el rol, enviem al Panell
                            Intent(this, PantallaPanell::class.java)
                        }
                    }

                    // 3. Iniciem la pantalla corresponent i cerquem el Login
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        error ?: "Error de connexió",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}