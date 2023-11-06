package com.packages.heatmap.walkscore

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.packages.heatmap.BuildConfig
import com.packages.heatmap.utils.LocationViewModel
import com.packages.heatmap.walkscore.api.Request
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
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
        private const val RED: Float = 360f
        private const val GREEN = 111f
        private const val ALPHA = 0.6f
        private const val MAX_SATURATION = 1f
        private val walkscoreColorMapping: HashMap<Int, Color> = hashMapOf(
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

        fun getColorByWalkscore(walkScore: Int): Color {
            Log.w("AppDataKlejdis", (walkScore - 1).toString())
            try {
                val walkScoreLevel = (walkScore - 1).toString()[0].digitToInt()
                return walkscoreColorMapping[walkScoreLevel]!!
            }
            catch(e: Exception){
                return Color.Transparent
            }
        }

        fun getListOfAreasThatContainPoint(
            point: LatLng,
            mapping: HashMap<LatLng, Area>
        ): ArrayList<Area> {
            val results = ArrayList<Area>()
            for (key: LatLng in mapping.keys) {
                if (mapping[key]!!.containsPoint(point)) {
                    results.add(mapping[key]!!)
                }
            }
            return results
        }
        fun getAreaFromAPIRequest(latitude: Double, longitude: Double, address: String? = ""): Thread {
            return Thread {
                val url = URL(
                    "https://api.walkscore.com/score?format=json&" +
                            "address=1119%8th%20Avenue%20Seattle%20WA%2098101&lat=${latitude}&" +
                            "lon=${longitude}&transit=1&bike=1&wsapikey=${BuildConfig.WALKSCORE_API_KEY}"
                )
                val connection = url.openConnection() as HttpURLConnection
                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, Request::class.java)
                    inputStreamReader.close()
                    inputSystem.close()
                    CircleArea(latitude, longitude, request.walkscore, address = address, description = request.description)
                }
                else
                    Log.w("Connection Error", "Failed to connect")
            }
        }
    }
    override fun toString(): String {
        return "Lat: $latitude, Long: $longitude, Walkscore: $walkscore"
    }
    internal abstract fun containsPoint(point: LatLng): Boolean
}