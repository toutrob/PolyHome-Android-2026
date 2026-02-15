package com.robin.polyhome

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bttnConnect = findViewById<Button>(R.id.bttnLogin)
        val bttnRetour = findViewById<Button>(R.id.bttnBack)

        bttnConnect?.setOnClickListener { view ->
            login(view)
        }

        bttnRetour?.setOnClickListener { view ->
            finish()
        }
    }


    val url = "https://polyhome.lesmoulinsdudev.com/api/users/auth"

    fun login(view: View) {
        val login = findViewById<EditText>(R.id.edtLoginName).text.toString()
        val password = findViewById<EditText>(R.id.edtLoginPassword).text.toString()
        val loginData = LoginData(login, password)
        val savedToken = TokenManager.getToken(this)

        if (login.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Incorrect, please fill the fields", Toast.LENGTH_SHORT).show()
            return
        }

        Api().post<LoginData, LoginResponse>(
            url,
            loginData,
            ::loginSuccess
        )
    }

    fun loginSuccess(responseCode: Int, token: LoginResponse?) {
        runOnUiThread {
            if (responseCode == 200 && token != null) {
                TokenManager.saveToken(this, token.token)

                Toast.makeText(this, "Connected !", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)

                intent.putExtra("token", token.token)
                startActivity(intent)

                finish()

            } else if (responseCode == 400) {
                Toast.makeText(this, "Data incorrect, try again", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 404) {
                Toast.makeText(
                    this,
                    "User unknow ! Are you sure you are from Polytech ??",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Internal Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}