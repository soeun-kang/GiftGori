package com.example.tabs.utils.models

import java.util.Date

data class Contact(
    val name: String,
    val phoneNumber: String,
    val bDay: Date,
    val gender: String,
    val group: String,
    val occasions: List<Occasion>,
    val recentContact: Int,
    val presentHistory: List<PresentHistory>
)