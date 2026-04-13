package com.example.eventmanagerapp.Model

data class Organizer(
    val uid: String = " ",
    val name: String = "",
    val location: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val priceRange: String = "",
    val portfolioDescription: String = "",
    val portfolioImages: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val socialLinks: List<String> = emptyList()
)
