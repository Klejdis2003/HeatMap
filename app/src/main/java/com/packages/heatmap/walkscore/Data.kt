package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import com.opencsv.CSVReader

fun buildHashMap(csvReader: CSVReader): Map<LatLng, HexagonArea> {
    var nextLine: Array<String>?
    while (csvReader.readNext().also { nextLine = it } != null) {
        try {
            val latitude = nextLine!![1].toDouble()
            val longitude = nextLine!![2].toDouble()
            val walkScore = nextLine!![3].toInt()
            val description = nextLine!![4]
            HexagonArea(latitude, longitude, walkScore, description = description)
        } catch (e: Exception) {
            //do nothing
        }
    }
    return HexagonArea.mapping
}