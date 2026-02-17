package com.robin.polyhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    lateinit var housesAdapter: ArrayAdapter<HouseData>
    private val houses: ArrayList<HouseData> = ArrayList()
    private var token: String? = null

    private lateinit var bttnGuests: Button
    private lateinit var spinHouses: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        token = TokenManager.getToken(this)

        spinHouses = findViewById(R.id.spinHouses)
        bttnGuests = findViewById(R.id.btnGuests)
        val bttnDeconnexion = findViewById<Button>(R.id.bttnDisconnect)

        housesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, houses)
        spinHouses.adapter = housesAdapter

        spinHouses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkOwnerPermissions()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        bttnGuests.setOnClickListener {
            val selectedHouse = spinHouses.selectedItem as? HouseData
            if (selectedHouse != null) {
                val intent = Intent(this, GuestsActivity::class.java)
                intent.putExtra("HOUSE_ID", selectedHouse.houseId.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Aucune maison sélectionnée", Toast.LENGTH_SHORT).show()
            }
        }

        bttnDeconnexion.setOnClickListener {
            TokenManager.saveToken(this, "")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadHouses()
    }

    private fun checkOwnerPermissions() {
        val selectedHouse = spinHouses.selectedItem as? HouseData
        val myLogin = TokenManager.getLogin(this)

        if (selectedHouse != null && selectedHouse.owner) {
                bttnGuests.visibility = View.VISIBLE
        } else {
                bttnGuests.visibility = View.GONE
        }
    }

    private fun loadHouses() {
        Api().get<List<HouseData>>("https://polyhome.lesmoulinsdudev.com/api/houses", ::loadHousesSuccess, token)
    }

    private fun loadHousesSuccess(responseCode: Int, loadedHouses: List<HouseData>?) {
        if (responseCode == 200 && loadedHouses != null) {
            houses.clear() // Évite les doublons si on recharge
            houses.addAll(loadedHouses)

            runOnUiThread {
                housesAdapter.notifyDataSetChanged()
                checkOwnerPermissions()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Erreur chargement maisons ($responseCode)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goToLights(view: View) {
        openDevicesActivity("light")
    }

    fun goToShutters(view: View) {
        openDevicesActivity("shutter")
    }

    private fun openDevicesActivity(type: String) {
        val selected = spinHouses.selectedItem as? HouseData

        if (selected != null) {
            val intent = Intent(this, DevicesActivity::class.java)
            intent.putExtra("owner", selected.owner)
            intent.putExtra("houseId", selected.houseId.toString())
            intent.putExtra("filter", type)
            startActivity(intent)
        }
    }
}