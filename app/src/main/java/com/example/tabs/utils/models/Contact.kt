package com.example.tabs.utils.models

import java.util.Date

data class Contact(
    val name: String,
    val phoneNumber: String,
    val bDay: Date,
    val recentContact: Int,
    val presentHistory: List<PresentHistory>
)