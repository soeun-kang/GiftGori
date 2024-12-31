package com.example.tabs.utils.models

import java.io.Serializable
import java.util.Date

data class Contact(
    var name: String,
    var phoneNumber: String,
    var bDay: Date,
    var gender: String,
    var group: String,
    var occasions: List<Occasion>,
    var recentContact: Int,
    var presentHistory: List<PresentHistory>
) : Serializable