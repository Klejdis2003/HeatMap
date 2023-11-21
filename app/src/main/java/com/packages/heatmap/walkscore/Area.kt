package com.packages.heatmap.walkscore

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

abstract class Area (
    var latitude:Double,
    var longitude:Double,
    var walkscore: Int,
    val city : String = "Washington DC",
    var address: String? = "",
    var description: String? = ""
)

{
    constructor() : this(0.0, 0.0, 0)
    init {
        mapping[LatLng(this.latitude, this.longitude)] = this
    }
    companion object {
        private val NO_WALKSCORE_DATA = 0
        private const val RED: Float = 360f
        private const val GREEN = 111f
        private const val ALPHA = 0.6f
        private const val MAX_SATURATION = 1f
        private val colorMap: HashMap<Int, Color> = hashMapOf(
            0 to Color.hsl(0f, 0f, 0.5f, ALPHA), //gray
            1 to Color.hsl(RED, MAX_SATURATION, 0.22f, ALPHA), //darkest red
            2 to Color.hsl(RED, MAX_SATURATION, 0.3f, ALPHA),
            3 to Color.hsl(RED, MAX_SATURATION, 0.38f, ALPHA),
            4 to Color.hsl(RED, MAX_SATURATION, 0.30f, ALPHA),
            5 to Color.hsl(1f, MAX_SATURATION, 0.69f, ALPHA),
            6 to Color.hsl(GREEN, MAX_SATURATION, 0.72f, ALPHA),
            7 to Color.hsl(GREEN, MAX_SATURATION, 0.57f, ALPHA),
            8 to Color.hsl(GREEN, MAX_SATURATION, 0.42f, ALPHA),
            9 to Color.hsl(GREEN, MAX_SATURATION, 0.27f, ALPHA) // most vibrant green
        )

        var mapping: HashMap<LatLng, Area> = HashMap()

        /**
         * Computes the distance between two points on the earth, taking the curvature into account.
         * @param p1 The first point.
         * @param p2 The second point.
         * @return The distance between the two points in meters.
         */
        internal fun computePointDistance(p1: LatLng, p2: LatLng): Double {
            val R: Double = 6.371 * 10.0.pow(6.0) //radius of the erarth in meters
            val latRadians: Double =
                p1.latitude * Math.PI / 180.0 //latitude of center in Radians
            val pointLatRadians: Double =
                p2.latitude * Math.PI / 180.0 //latitude of point in radians
            val latDifference: Double =
                latRadians - pointLatRadians //the angle difference of center and point latitudes
            val longDifference: Double =
                (p1.longitude - p2.longitude) * Math.PI / 180.0 //the angle diff between center and point longitudees

            val firstSinTerm: Double = sin((latDifference) / 2.0).pow(2) //first haversine
            val secondSinTerm: Double = sin((longDifference) / 2.0).pow(2) //second haversine

            return 2 * R * asin(
                sqrt(firstSinTerm + cos(latRadians) * cos(pointLatRadians) * secondSinTerm)
            )
        }

        /**
         * @param walkScore The walkscore value.
         * @return The color that corresponds to that value in the heatmap.
         */
        fun getColorByWalkscore(walkScore: Int): Color {
            if(walkScore == NO_WALKSCORE_DATA)
                return colorMap[0]!!
            val walkScoreLevel = (walkScore - 1).toString()[0].digitToInt()
            return colorMap[walkScoreLevel]!!

        }

//        /**
//         * @param point The point to check.
//         * @return A list of Areas that contain the point.
//         */
//        fun getListOfAreasThatContainPoint(point: LatLng): ArrayList<Area> {
//            val results = ArrayList<Area>()
//            for (key: LatLng in mapping.keys) {
//                if (mapping[key]!!.containsPoint(point)) {
//                    results.add(mapping[key]!!)
//                }
//            }
//            return results
//        }
    }

    override fun toString(): String {
        return "Lat: $latitude, Long: $longitude, Walkscore: $walkscore"
    }

    /**
     * The implementation of this method depends on the shape chosen to represent the area. Must be overidden
     * by child classes.
     * @param point The point to check.
     * @return True if the point is contained in the area, false otherwise.
     */
    internal abstract fun containsPoint(point: LatLng): Boolean
}