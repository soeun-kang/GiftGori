package com.example.tabs.utils.models

import java.util.Date
import com.example.tabs.utils.models.PresentHistory

data class Contact(
    val name: String,
    val phoneNumber: String,
    val bDay: Date,
    val gender: String,
    val recentContact: Int,
    val presentHistory: List<PresentHistory>
)