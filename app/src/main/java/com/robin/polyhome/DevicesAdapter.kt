package com.robin.polyhome

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

class DevicesAdapter(
    context: Context,
    private val devices: ArrayList<DeviceData>,
    private val onCommandClick: (String, String) -> Unit
) : ArrayAdapter<DeviceData>(context, 0, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_device, parent, false)

        val device = getItem(position)

        val layout = rowView.findViewById<LinearLayout>(R.id.layoutDeviceItem)
        val txtName = rowView.findViewById<TextView>(R.id.txtDeviceName)
        val txtIcon = rowView.findViewById<TextView>(R.id.txtIcon)
        val txtStatus = rowView.findViewById<TextView>(R.id.txtStatus)

        if (device != null) {
            txtName.text = device.id

            if (device.type == "light") {
                txtIcon.text = "💡"
                val isOn = (device.power ?: 0) > 0

                if (isOn) {
                    layout.setBackgroundColor(Color.parseColor("#FFF176"))
                    txtStatus.text = "ALLUMÉ"
                } else {
                    layout.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    txtStatus.text = "ÉTEINT"
                }

                layout.setOnClickListener {
                    val command = if (isOn) "TURN OFF" else "TURN ON"
                    onCommandClick(device.id, command)
                }
            }
            else {
                txtIcon.text = "🪟"
                val isOpen = (device.opening ?: 0) > 0

                if (isOpen) {
                    layout.setBackgroundColor(Color.parseColor("#81C784"))
                    txtStatus.text = "OUVERT"
                } else {
                    layout.setBackgroundColor(Color.parseColor("#E0E0E0"))
                    txtStatus.text = "FERMÉ"
                }

                layout.setOnClickListener {
                    val command = if (isOpen) "CLOSE" else "OPEN"
                    onCommandClick(device.id, command)
                }
            }
        }
        return rowView
    }
}