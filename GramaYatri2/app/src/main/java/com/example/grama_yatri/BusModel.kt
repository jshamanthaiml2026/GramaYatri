package com.example.grama_yatri

import java.io.Serializable

data class BusStopInfo(
    val name: String,
    val time: String,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false
) : Serializable

data class BusModel(
    val name: String,
    val time: String,
    val route: String,
    val type: String,
    val seats: Int,
    val price: String,
    val stops: List<BusStopInfo>
) : Serializable
