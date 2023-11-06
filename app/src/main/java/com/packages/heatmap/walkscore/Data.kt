package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import com.opencsv.CSVReader
import com.packages.heatmap.utils.LocationViewModel

fun buildHashMap(csvReader: CSVReader): HashMap<LatLng, CircleArea> {
    var nextLine: Array<String>?
    var area: Area;
    while (csvReader.readNext().also { nextLine = it } != null) {
        try {
            val latitude = nextLine!![1].toDouble()
            val longitude = nextLine!![2].toDouble()
            val walkScore = nextLine!![3].toInt()
            val description = nextLine!![4]
            CircleArea(latitude, longitude, walkScore, description = description)
        } catch (e: Exception) {
            //do nothing
        }
    }
    return CircleArea.mapping
}