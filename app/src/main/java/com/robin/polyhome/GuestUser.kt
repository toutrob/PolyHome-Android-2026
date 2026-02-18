package com.robin.polyhome

data class GuestUser(
    val userLogin: String
)

data class GuestUserResponse(
    val userLogin: String,
    val owner: Int
)
