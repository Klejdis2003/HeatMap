package com.packages.heatmap.walkscore

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.annotations.NotNull

class WalkScoreInformation (
    val city: String,
    val latitude:Double,
    val longitude:Double,
    val walkScore: Int
) {
    companion object{
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

        var mapping: HashMap<LatLng, WalkScoreInformation> = HashMap()
        fun getColorByWalkscore(walkScore: Int): Color{
            Log.w("AppDataKlejdis", (walkScore-1).toString())
            val walkScoreLevel =  (walkScore-1).toString()[0].digitToInt()
            return walkscoreColorMapping[walkScoreLevel]!!
        }
    }
    init{
       mapping[LatLng(this.latitude, this.longitude)] = this
    }


    override fun toString(): String {
        return "WalkScoreInformation(city='$city')"
    }


}

