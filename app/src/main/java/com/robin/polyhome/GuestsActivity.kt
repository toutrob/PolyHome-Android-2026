package com.robin.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class GuestsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guests)

        val houseId = intent.getStringExtra("HOUSE_ID")
        val bttnGuests = findViewById<Button>(R.id.bttnAddGuests)

        bttnGuests?.setOnClickListener {
            val guestsName = findViewById<EditText>(R.id.edtGuestsName).text.toString()
            if (guestsName.isNotEmpty() && houseId != null) {
                addGuest(houseId, guestsName)
            } else {
                Toast.makeText(this, "Eror : field empty or house unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addGuest(houseId: String, username: String){
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        val body = GuestUser(username)
        val token = TokenManager.getToken(this)

        Api().post(
            url,
            body,
            ::onInviteResult,
            token
        )
    }

    private fun onInviteResult(responseCode: Int){
        runOnUiThread{
            if (responseCode == 200) {
                Toast.makeText(this, "Adding guest successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else if (responseCode == 400){
                Toast.makeText(this, "Data incorrect! Make sure you have enter the good information", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 403){
                Toast.makeText(this, "Unauthorized Action! You are not the landlord of the ring (i'm not sure about this)", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 409){
                Toast.makeText(this, "Guest already associated", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 500){
                Toast.makeText(this, "Internal Error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unknow error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}