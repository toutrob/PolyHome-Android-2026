package com.robin.polyhome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

class HomeActivity : AppCompatActivity() {
    // On prépare l'adaptateur pour la liste déroulante (Spinner)
    lateinit var housesAdapter: ArrayAdapter<HouseData>
    private val houses: ArrayList<HouseData> = ArrayList()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // On récupère le token
        token = intent.getStringExtra("token")

        // On configure la liste déroulante
        housesAdapter = ArrayAdapter<HouseData>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, houses)
        val spinHouses = findViewById<Spinner>(R.id.spinHouses)
        spinHouses.adapter = housesAdapter

        // On charge la liste des maisons
        loadHouses()
    }

    private fun loadHouses() {
        Api().get<List<HouseData>>(
            "https://polyhome.lesmoulinsdudev.com/api/houses",
            ::loadHousesSuccess,
            token
        )
    }

    private fun loadHousesSuccess(responseCode: Int, loadedHouses: List<HouseData>?) {
        if (responseCode == 200 && loadedHouses != null) {
            houses.clear()
            houses.addAll(loadedHouses)

            // On met à jour l'interface sur le thread principal
            runOnUiThread {
                housesAdapter.notifyDataSetChanged()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Erreur lors du chargement des maisons", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun goToLights(view: View) {
        openDevicesActivity("light")
    }

    public fun goToShutters(view: View) {
        openDevicesActivity("shutter")
    }

    // Fonction pour ouvrir la page des périphériques
    private fun openDevicesActivity(deviceTypeFilter: String) {
        val spinHouses = findViewById<Spinner>(R.id.spinHouses)

        val selectedHouse = spinHouses.selectedItem as? HouseData

        if (selectedHouse != null && token != null) {
            val intent = Intent(this, DevicesActivity::class.java)
            intent.putExtra("token", token)
            intent.putExtra("houseId", selectedHouse.houseId.toString())
            intent.putExtra("filter", deviceTypeFilter) // On passe le filtre (lampe ou volet)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Veuillez sélectionner une maison", Toast.LENGTH_SHORT).show()
        }
    }

    public fun goToGuests(view: View) {
        val spinHouses = findViewById<Spinner>(R.id.spinHouses)
        val selectedHouse = spinHouses.selectedItem as? HouseData

        if (selectedHouse != null && token != null) {
            if (selectedHouse.owner) {
                Toast.makeText(this, "Module Invités à venir", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Seul le propriétaire peut gérer les invités", Toast.LENGTH_LONG).show()
            }
        }
    }
}