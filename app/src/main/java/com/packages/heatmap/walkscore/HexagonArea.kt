package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng

class HexagonArea(latitude: Double, longitude: Double, walkScore: Int) : Area(latitude, longitude, walkScore) {
    private val nSides = 6

    override fun containsPoint(point: LatLng): Boolean {
        TODO("Not yet implemented")
    }

}
