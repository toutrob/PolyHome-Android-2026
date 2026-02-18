package com.robin.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.net.URLEncoder

class GuestsActivity : AppCompatActivity() {

    private var houseId: String? = null
    private var tokenGuest: String? = null
    private val guestList = ArrayList<GuestUserResponse>()
    private lateinit var listGuestView: ListView
    private lateinit var adapter: GuestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guests)

        houseId = intent.getStringExtra("HOUSE_ID")
        tokenGuest = TokenManager.getToken(this)

        val bttnGuests = findViewById<Button>(R.id.bttnAddGuests)
        val bttnBack = findViewById<Button>(R.id.bttnReturn)
        listGuestView = findViewById<ListView>(R.id.listViewGuest)

        adapter = GuestAdapter()
        listGuestView.adapter = adapter

        bttnGuests?.setOnClickListener {
            val edtGuestName = findViewById<EditText>(R.id.edtGuestsName)
            val guestsName = edtGuestName.text.toString()

            if (guestsName.isNotEmpty() && houseId != null) {
                addGuest(houseId!!, guestsName)
                edtGuestName.text.clear()
            } else {
                Toast.makeText(this, "Error : field empty or house unknown", Toast.LENGTH_SHORT).show()
            }
        }

        bttnBack?.setOnClickListener {
            finish()
        }

        loadGuests()
    }

    // --- ADAPTER ---
    private inner class GuestAdapter : BaseAdapter() {
        override fun getCount(): Int = guestList.size
        override fun getItem(position: Int) = guestList[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@GuestsActivity)
                .inflate(R.layout.item_guest, parent, false)

            val guest = guestList[position]

            val txtName = view.findViewById<TextView>(R.id.txtGuestName)

            val bttnDeleteGuest = view.findViewById<Button>(R.id.bttnDelete)

            if (guest.owner == 1) {
                txtName.text = "EL REY : ${guest.userLogin}"
                bttnDeleteGuest.visibility = View.INVISIBLE
            } else {
                txtName.text = "La populace : ${guest.userLogin}"
                bttnDeleteGuest.visibility = View.VISIBLE

                bttnDeleteGuest.setOnClickListener {
                    confirmDelete(guest.userLogin)
                }
            }
            return view
        }
    }

    private fun loadGuests() {
        if (houseId != null) {
            val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
            Api().get<List<GuestUserResponse>>(
                url,
                ::onGuestsLoaded,
                tokenGuest
            )
        }
    }

    private fun onGuestsLoaded(responseCode: Int, guests: List<GuestUserResponse>?) {
        runOnUiThread {
            if (responseCode == 200 && guests != null) {
                guestList.clear()
                guestList.addAll(guests)
                adapter.notifyDataSetChanged() // Rafraîchir l'affichage
            } else {
                Toast.makeText(this, "Error loading user ($responseCode)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDelete(username: String) {
        AlertDialog.Builder(this)
            .setTitle("Bannissement")
            .setMessage("Voulez-vous vraiment bannir $username ?")
            .setPositiveButton("Oui") { _, _ -> removeGuest(username) }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun removeGuest(username: String) {
        val encodedName = URLEncoder.encode(username, "UTF-8").replace("+", "%20")
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users/$encodedName"

        Api().delete(url, onSuccess = { responseCode ->
            runOnUiThread {
                if (responseCode == 200) {
                    Toast.makeText(this, "Invité supprimé", Toast.LENGTH_SHORT).show()
                    loadGuests()
                } else if (responseCode == 400) {
                    Toast.makeText(this, "Data incorrect", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 403){
                    Toast.makeText(this, "Unauthorized acces", Toast.LENGTH_SHORT).show()
                } else if (responseCode == 500) {
                    Toast.makeText(this, "Internal Error", Toast.LENGTH_SHORT).show()
                }
            }
        }, securityToken = tokenGuest)
    }

    private fun addGuest(houseId: String, username: String) {
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        val body = GuestUser(username) // Data class GuestUser(val userLogin: String)

        Api().post(
            url,
            body,
            ::onInviteResult,
            tokenGuest
        )
    }

    private fun onInviteResult(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Adding guest successfully!", Toast.LENGTH_SHORT).show()
                loadGuests()
            } else if (responseCode == 400) {
                Toast.makeText(this, "Data incorrect!", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 403) {
                Toast.makeText(this, "Unauthorized Action! Not the landlord.", Toast.LENGTH_SHORT).show()
            } else if (responseCode == 409) {
                Toast.makeText(this, "Guest already associated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: $responseCode", Toast.LENGTH_SHORT).show()
            }
        }
    }
}