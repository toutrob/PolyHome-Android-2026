package com.robin.polyhome

data class HouseData(
    val houseId: String,
    val owner: Boolean
) {
    override fun toString(): String {
        return "Maison $houseId" + if(owner) " (Propriétaire)" else " (Invité)"
    }
}