package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt

class HexagonArea() : Area() {
    private val radius : Double = 50.0

    companion object{
        val mapping: HashMap<LatLng, HexagonArea> = HashMap()
    }

    constructor(latitude: Double, longitude: Double, walkScore: Int, address: String? = "", description: String? = "") : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.walkscore = walkScore
        this.address = address
        this.description = description
        mapping[LatLng(latitude, longitude)] = this
    }

    fun getPoints(): MutableList<LatLng> {
        /*
            Source: https://www.quora.com/How-can-you-find-the-coordinates-in-a-hexagon
         */
        val points: MutableList<LatLng> = MutableList(6) { LatLng(latitude, longitude) }
        val degreeRadius = radius / 111111 // 111111 meters is approx equiv to 1 degree
        points[0] = LatLng(latitude + (degreeRadius), longitude)
        points[1] = LatLng(latitude + (degreeRadius/2), longitude - ((sqrt(3.0) * degreeRadius)/2))
        points[2] = LatLng(latitude - (degreeRadius/2), longitude - ((sqrt(3.0) * degreeRadius)/2))
        points[3] = LatLng(latitude - degreeRadius, longitude)
        points[4] = LatLng(latitude - (degreeRadius/2), longitude + ((sqrt(3.0) * degreeRadius)/2))
        points[5] = LatLng(latitude + (degreeRadius/2), longitude + ((sqrt(3.0) * degreeRadius)/2))

        return points
    }

    override fun containsPoint(point: LatLng): Boolean {
        /*
            TODO(THIS IS JUST THE CIRCLE METHOD, so some (literally) edge cases will be wrong)
         */
        val distance = computePointDistance(LatLng(this.latitude, this.longitude), point)
        return distance <= radius
    }

}
