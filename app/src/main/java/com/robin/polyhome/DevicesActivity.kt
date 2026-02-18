package com.robin.polyhome

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DevicesActivity : AppCompatActivity() {
    private val devicesList: ArrayList<DeviceData> = ArrayList()
    private lateinit var adapter: DevicesAdapter

    private var token: String? = null
    private var houseId: String? = null
    private var filterType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        token = TokenManager.getToken(this)
        houseId = intent.getStringExtra("houseId")
        filterType = intent.getStringExtra("filter")

        val title = findViewById<TextView>(R.id.txtCategoryTitle)
        val btnAllOn = findViewById<Button>(R.id.btnAllOn)
        val btnAllOff = findViewById<Button>(R.id.btnAllOff)

        if (filterType == "light"){
            title.text = "Lumières"
            btnAllOn.text = "Tout Allumer"
            btnAllOff.text = "Tout Éteindre"

            btnAllOn.setOnClickListener {sendGlobalCommand("TURN ON")}
            btnAllOff.setOnClickListener {sendGlobalCommand("TURN OFF")}
        } else {
            title.text = "Volets"
            btnAllOn.text = "Tout Ouvrir"
            btnAllOff.text = "Tout Fermer"

            btnAllOn.setOnClickListener {sendGlobalCommand("OPEN") }
            btnAllOff.setOnClickListener {sendGlobalCommand("CLOSE")}
        }

        val gridView = findViewById<GridView>(R.id.gridDevices)

        adapter = DevicesAdapter(this, devicesList, ::sendCommand, ::showRenameDialog)
        gridView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadDevices()
    }

    private fun renameDevice(device: DeviceData, newName: String) {
        val myHouseId = houseId
        if (myHouseId != null) {
            lifecycleScope.launch {
                val storage = DeviceStorage(this@DevicesActivity)
                storage.saveName(myHouseId, device.id, newName)

                device.customName = newName
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showRenameDialog(device: DeviceData) {
        val Input = EditText(this)
        if (device.customName != null) {
            Input.setText(device.customName)
        } else {
            Input.setText(device.id)
        }

        AlertDialog.Builder(this)
            .setTitle("Renommer l'appareil")
            .setView(Input)
            .setPositiveButton("Valider") { _, _ ->
                val newName = Input.text.toString()
                if (newName.isNotEmpty()) {
                    renameDevice(device, newName)
                }
            }
            .show()
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

            lifecycleScope.launch {
                val storage = DeviceStorage(this@DevicesActivity)

                devicesList.forEach { device ->
                    val savedName = storage.getName(houseId, device.id)
                    if (savedName != null) {
                        device.customName = savedName
                    }
                }

                adapter.notifyDataSetChanged()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Echec récupération liste", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendGlobalCommand(commandValue: String) {
        for (device in devicesList) {
            if (device.type == "light") {
                if (commandValue == "TURN ON"){
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
            val data = CommandData(command = commandValue)
            val url = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/${device.id}/command"
            Api().post(url, data, ::commandSuccess, token)
        }
        adapter.notifyDataSetChanged()
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