package com.robin.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val clickBtn = findViewById<Button>(R.id.bttnRegister)

        val bttnBackToMain = findViewById<Button>(R.id.bttnRetour)

        bttnBackToMain.setOnClickListener { view ->
            finish()
        }

        clickBtn.setOnClickListener { view ->
            Register(view)
        }
    }

    val urlRegister = "https://polyhome.lesmoulinsdudev.com/api/users/register"

    fun Register(view: View){
        val login = findViewById<EditText>(R.id.edtNewUserName).text.toString()
        val password = findViewById<EditText>(R.id.edtNwUsrPsswrd).text.toString()

        val registerData = RegisterData(login, password)

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please, fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        Api().post<RegisterData>(
            urlRegister,
            registerData,
            ::registerSuccess
        )
    }

    fun registerSuccess(responseCode: Int){
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                finish()
            }
            else if (responseCode == 400){
                Toast.makeText(this, "Data incorrect, try again !", Toast.LENGTH_SHORT).show()
            }
            else if (responseCode == 409){
                Toast.makeText(this, "Login already use, try another name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error Server : $responseCode", Toast.LENGTH_SHORT).show()
            }

        }
    }
}