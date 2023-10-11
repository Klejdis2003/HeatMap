package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import org.jetbrains.annotations.NotNull

class WalkScoreInformation (
    val city: String,
    val latitude:Double,
    val longitude:Double,
    val walkScore: Int
) {
    companion object{

        var mapping: HashMap<LatLng, WalkScoreInformation> = HashMap()
    }
    init{
       mapping[LatLng(this.latitude, this.longitude)] = this
    }
    override fun toString(): String {
        return "WalkScoreInformation(city='$city')"
    }


}

