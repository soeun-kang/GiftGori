package com.example.tabs.utils.models

import java.io.Serializable
import java.util.Date

data class PresentHistory(
    val date: Date,
    val gift: String,
    val price: Int
) : Serializable