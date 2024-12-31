package com.example.tabs.utils.models

import java.io.Serializable
import java.util.Date

data class Occasion(
    val occasion: String,
    val date: Date
) : Serializable