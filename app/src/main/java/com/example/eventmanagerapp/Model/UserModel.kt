package com.example.eventmanagerapp.Model

data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // "client", "organizer", "admin"
)
