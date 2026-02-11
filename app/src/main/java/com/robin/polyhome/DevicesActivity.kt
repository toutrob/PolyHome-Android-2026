package com.robin.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast

class DevicesActivity : AppCompatActivity() {
    private val devicesList: ArrayList<DeviceData> = ArrayList()
    private lateinit var adapter: DevicesAdapter

    private var token: String? = null
    private var houseId: String? = null
    private var filterType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        token = intent.getStringExtra("token")
        houseId = intent.getStringExtra("houseId")
        filterType = intent.getStringExtra("filter")

        val title = findViewById<TextView>(R.id.txtCategoryTitle)
        if (filterType == "light") title.text = "Mes Lumières"
        else title.text = "Mes Volets"

        val gridView = findViewById<GridView>(R.id.gridDevices)

        adapter = DevicesAdapter(this, devicesList, ::sendCommand)
        gridView.adapter = adapter

        loadDevices()
    }

    private fun loadDevices() {
        if (houseId != null && token != null) {
            val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
            Api().get<List<DeviceData>>(url, ::loadDevicesSuccess, token)
        }
    }

    private fun loadDevicesSuccess(responseCode: Int, loadedDevices: List<DeviceData>?) {
        if (responseCode == 200 && loadedDevices != null) {
            devicesList.clear()
            for (device in loadedDevices) {
                if (filterType == "light" && device.type == "light") {
                    devicesList.add(device)
                }
                else if (filterType == "shutter" && (device.type == "shutter" || device.type == "opening")) {
                    devicesList.add(device)
                }
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Erreur chargement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendCommand(deviceId: String, commandValue: String) {
        if (houseId != null && token != null) {
            val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        }
    }

    private fun commandSuccess(responseCode: Int) {
        runOnUiThread {
            if (responseCode == 200) {
                Toast.makeText(this, "Action effectuée", Toast.LENGTH_SHORT).show()
                loadDevices()
            } else {
                Toast.makeText(this, "Erreur ($responseCode)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}