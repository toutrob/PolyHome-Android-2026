package com.robin.polyhome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        if (filterType == "light"){
            title.text = "Lumières"
        } else {
            title.text = "Volets"
        }

        val gridView = findViewById<GridView>(R.id.gridDevices)

        adapter = DevicesAdapter(this, devicesList, ::sendCommand)
        gridView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Recharge la liste à chaque fois qu'on revient sur l'écran
        loadDevices()
    }

    private fun loadDevices() {
        if (houseId != null && token != null) {
            val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"
            Api().get<DevicesResponse>(url, ::loadDevicesSuccess, token)
        }
    }

    private fun loadDevicesSuccess(responseCode: Int, response: DevicesResponse?) {
        if (responseCode == 200 && response != null) {
            devicesList.clear()

            val list = response.devices
            for (device in list) {
                if (filterType == "light" && device.type == "light") {
                    devicesList.add(device)
                }
                else if (filterType == "shutter" && (device.type == "rolling shutter" || device.type == "garage door")) {
                    devicesList.add(device)
                }
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Echec récupération liste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendCommand(deviceId: String, commandValue: String) {
        for (device in devicesList) {
            if (device.id == deviceId) {
                if (device.type == "light") {
                    if (commandValue == "TURN ON") {
                        device.power = 1.0
                    } else {
                        device.power = 0.0
                    }
                } else {
                    if (commandValue == "OPEN") {
                        device.opening = 1.0
                    } else {
                        device.opening = 0.0
                    }
                }
            }
        }

        adapter.notifyDataSetChanged()

        val data = CommandData(command = commandValue)
        val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        Api().post(url, data, ::commandSuccess, token)
    }

    private fun commandSuccess(responseCode: Int) {
        if (responseCode != 200) {
            runOnUiThread {
                Toast.makeText(this, "Echec commande", Toast.LENGTH_SHORT).show()
                loadDevices()
            }
        }
    }
}