package com.robin.polyhome

data class DeviceData(
    val id: String,
    val type: String,
    val availableCommands: ArrayList<String>,
    var power: Double?,
    var opening: Double?,
    var customName: String? = null
)

data class DevicesResponse(
    val devices: List<DeviceData>
)