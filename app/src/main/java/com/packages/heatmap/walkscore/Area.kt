package com.packages.heatmap.walkscore

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import java.util.TreeSet
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Area (
    val city: String,
    val latitude:Double,
    val longitude:Double,
    val walkScore: Int,
    val radius: Double = 800.0 //meters
) {
    companion object {
        private val walkscoreColorMapping: HashMap<Int, Color> = hashMapOf(
            1 to Color.hsl(360f, 1f, 0.22f, 0.4f),
            2 to Color.hsl(360f, 1f, 0.3f, 0.4f),
            3 to Color.hsl(360f, 1f, 0.38f, 0.4f),
            4 to Color.hsl(360f, 1f, 0.30f, 0.4f),
            5 to Color.hsl(1f, 1f, 0.69f, 0.4f),
            6 to Color.hsl(111f, 1f, 0.72f, 0.4f),
            7 to Color.hsl(111f, 1f, 0.57f, 0.4f),
            8 to Color.hsl(111f, 1f, 0.42f, 0.4f),
            9 to Color.hsl(111f, 1f, 0.27f, 0.4f),
        )

        var mapping: HashMap<LatLng, Area> = HashMap()
        fun getColorByWalkscore(walkScore: Int): Color {
            Log.w("AppDataKlejdis", (walkScore - 1).toString())
            val walkScoreLevel = (walkScore - 1).toString()[0].digitToInt()
            return walkscoreColorMapping[walkScoreLevel]!!
        }

        fun getListOfAreasThatContainPoint(point: LatLng): ArrayList<Area> {
            val results = ArrayList<Area>()
            for (key: LatLng in mapping.keys) {
                if (mapping[key]!!.containsPoint(point)) {
                    results.add(mapping[key]!!)
                }
            }
            return results
        }
    }

    init {
        mapping[LatLng(this.latitude, this.longitude)] = this
    }


    override fun toString(): String {
        return "(wk = $walkScore)"
    }

    private fun containsPoint(point: LatLng): Boolean {

        val R: Double = 6.371 * 10.0.pow(6.0) //radius of the erath in meters
        val latRadians:Double = latitude * Math.PI /180.0; //latitude of center in Radians
        val pointLatRadians:Double = point.latitude * Math.PI/180.0 //latitude of point in radians
        val latDifference: Double = latRadians - pointLatRadians //the angle difference of center and point latitudes
        val longDifference: Double = (longitude - point.longitude) * Math.PI / 180 //the angle diff between center and point longitudees

        val firstSinTerm: Double = sin((latDifference) / 2.0).pow(2) //first haversine
        val secondSinTerm: Double = sin((longDifference) / 2.0).pow(2) //second haversine

        return 2 * R * asin(
            sqrt(firstSinTerm + cos(latRadians) * cos(pointLatRadians) * secondSinTerm)) <= radius

    }
}