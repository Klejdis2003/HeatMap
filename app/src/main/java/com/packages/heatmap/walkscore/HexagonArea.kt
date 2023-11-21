package com.packages.heatmap.walkscore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sqrt

class HexagonArea() : Area() {
    val radius : Double = 100.0
    private val degreeRadius : Double = radius / 111111 // 111111 meters is approx equiv to 1 degree
    private val points: MutableList<LatLng> = MutableList(6) { LatLng(latitude, longitude) }
    private val neighbors: MutableList<LatLng> = MutableList(6) { LatLng(latitude, longitude) }

    companion object{
        var mapping by mutableStateOf(emptyMap<LatLng, HexagonArea>())
    }

    constructor(latitude: Double, longitude: Double, walkScore: Int, address: String? = "", description: String? = "") : this() {
        this.latitude = latitude
        this.longitude = longitude
        this.walkscore = walkScore
        this.address = address
        this.description = description
        addHex()
        makePoints()
        makeNeighbors()
    }

    private fun addHex() {
        mapping += Pair(LatLng(latitude, longitude), this)
    }

    /**
     * Uses the center of this object to generate the coordinates of the hexagon.
     */
    private fun makePoints() {
        /*
            Source: https://www.quora.com/How-can-you-find-the-coordinates-in-a-hexagon
         */
        points[0] = LatLng(latitude, longitude + (degreeRadius))
        points[1] = LatLng(latitude - ((sqrt(3.0) * degreeRadius)/2), longitude + (degreeRadius/2))
        points[2] = LatLng(latitude - ((sqrt(3.0) * degreeRadius)/2), longitude - (degreeRadius/2))
        points[3] = LatLng(latitude, longitude - degreeRadius)
        points[4] = LatLng(latitude + ((sqrt(3.0) * degreeRadius)/2), longitude - (degreeRadius/2))
        points[5] = LatLng(latitude + ((sqrt(3.0) * degreeRadius)/2), longitude + (degreeRadius/2))

    }

    /**
     Uses the center of this object to generate the coordinates of other neighbor hexagons.
     It calls itself recursively a set number of times.
     */
    private fun makeNeighbors(latitude: Double = this.latitude, longitude: Double = this.longitude, nTimes: Int = 0) {
        /*
            Source: https://www.redblobgames.com/grids/hexagons/
         */

        if(nTimes == 3)
            return
        val currentNeighbors = ArrayList<LatLng>()
        currentNeighbors.add( LatLng(latitude + (degreeRadius * 0.85), longitude + (degreeRadius * 1.5)))
        currentNeighbors.add(LatLng(latitude + (degreeRadius * 0.85), longitude - (degreeRadius * 1.5)))
        currentNeighbors.add(LatLng(latitude - (degreeRadius * 0.85), longitude - (degreeRadius * 1.5)))
        currentNeighbors.add(LatLng(latitude - (degreeRadius * 0.85), longitude + (degreeRadius * 1.5)))
        currentNeighbors.add(LatLng(latitude + (degreeRadius * 1.7), longitude))
        currentNeighbors.add(LatLng(latitude - (degreeRadius * 1.7), longitude))

        for(neighbor in currentNeighbors){
            neighbors.add(neighbor)
            makeNeighbors(neighbor.latitude, neighbor.longitude, nTimes + 1)
        }


    }

    fun getPoints() : MutableList<LatLng> {
        return points
    }

    fun getNeighbors() : MutableList<LatLng> {
        return neighbors
    }

    override fun containsPoint(point: LatLng): Boolean {
        /*
            TODO(THIS IS JUST THE CIRCLE METHOD, so some (literally) edge cases will be wrong)
         */
        val distance = computePointDistance(LatLng(this.latitude, this.longitude), point)
        return distance <= radius
    }

}
