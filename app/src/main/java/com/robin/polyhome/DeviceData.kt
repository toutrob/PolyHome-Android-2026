package com.robin.polyhome

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: ArrayList<String>,
    var power: Int?,
    var opening: Int?
)