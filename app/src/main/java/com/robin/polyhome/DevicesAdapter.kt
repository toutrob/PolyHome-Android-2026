package com.robin.polyhome

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class DevicesAdapter(context: Context, values: ArrayList<DeviceData>, private val onCommandClick: (String, String) -> Unit, private val onRename: (DeviceData) -> Unit) : ArrayAdapter<DeviceData>(context, 0, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_device, parent, false)

        val device = getItem(position)

        val layout = rowView.findViewById<LinearLayout>(R.id.layoutDeviceItem)
        val Name = rowView.findViewById<TextView>(R.id.txtDeviceName)
        val imgIcon = rowView.findViewById<ImageView>(R.id.imgIcon)
        val txtStatus = rowView.findViewById<TextView>(R.id.txtStatus)

        if (device != null) {
            if (device.customName != null) {
                Name.text = device.customName
            } else {
                Name.text = device.id
            }

            layout.setOnLongClickListener {
                onRename(device)
                true
            }

            if (device.type == "light") {
                val isOn = (device.power ?: 0.0) > 0

                if (isOn) {
                    imgIcon.setImageResource(R.drawable.light_on)
                    imgIcon.setColorFilter(Color.parseColor("#FBC02D"))
                    txtStatus.text = "ALLUMÉ"
                } else {
                    imgIcon.setImageResource(R.drawable.light_off)
                    imgIcon.setColorFilter(Color.LTGRAY)
                    txtStatus.text = "ÉTEINT"
                }

                layout.setOnClickListener {
                    var command = "TURN ON"
                    if (isOn) {
                        command = "TURN OFF"
                    }
                    onCommandClick(device.id, command)
                }
            }
            else if (device.type == "rolling shutter") {
                val isOpen = (device.opening ?: 0.0) > 0

                if (isOpen) {
                    imgIcon.setImageResource(R.drawable.shutter_open)
                    imgIcon.setColorFilter(Color.parseColor("#42A5F5"))
                    txtStatus.text = "OUVERT"
                } else {
                    imgIcon.setImageResource(R.drawable.shutter)
                    imgIcon.setColorFilter(Color.LTGRAY)
                    txtStatus.text = "FERMÉ"
                }

                layout.setOnClickListener {
                    var command = "OPEN"
                    if (isOpen) {
                        command = "CLOSE"
                    }
                    onCommandClick(device.id, command)
                }
            }
            else {
                val isOpen = (device.opening ?: 0.0) > 0

                if (isOpen) {
                    imgIcon.setImageResource(R.drawable.garage_open)
                    imgIcon.setColorFilter(Color.parseColor("#42A5F5"))
                    txtStatus.text = "OUVERT"
                } else {
                    imgIcon.setImageResource(R.drawable.garage)
                    imgIcon.setColorFilter(Color.LTGRAY)
                    txtStatus.text = "FERMÉ"
                }

                layout.setOnClickListener {
                    var command = "OPEN"
                    if (isOpen) {
                        command = "CLOSE"
                    }
                    onCommandClick(device.id, command)
                }
            }
        }
        return rowView
    }
}