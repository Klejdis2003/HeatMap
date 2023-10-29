package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng

class CircleArea() : Area() {
    val radius : Double = 500.0 //radius in meters

    companion object{
        val mapping: HashMap<LatLng, CircleArea> = HashMap()
    }
    init {
        mapping[LatLng(this.latitude, this.longitude)] = this
    }
    constructor(latitude: Double, longitude: Double, walkScore: Int) : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.walkScore = walkScore
        mapping[LatLng(latitude, longitude)] = this
    }
    override fun containsPoint(point: LatLng): Boolean {
        val distance = computePointDistance(LatLng(this.latitude, this.longitude), point)
        return distance <= radius
    }
}