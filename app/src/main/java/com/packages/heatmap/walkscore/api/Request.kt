package com.packages.heatmap.walkscore.api

data class Request(
    val walkscore: Int,
    val description: String,
    val snapped_lat: Double,
    val snappped_long: Double,
    val transit: Transit
)