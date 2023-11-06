package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng


class CircleArea() : Area(){
    val radius : Double = 50.0 //radius in meters
    companion object{
        val mapping: HashMap<LatLng, CircleArea> = HashMap()

    }
    constructor(latitude: Double, longitude: Double, walkScore: Int, address: String? = "", description: String? = "") : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.walkscore = walkScore
        this.address = address
        this.description = description
        mapping[LatLng(latitude, longitude)] = this
    }
    override fun containsPoint(point: LatLng): Boolean {
        val distance = computePointDistance(LatLng(this.latitude, this.longitude), point)
        return distance <= radius
    }
}