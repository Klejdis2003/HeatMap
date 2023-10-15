package com.packages.heatmap.walkscore

import com.google.android.gms.maps.model.LatLng
import com.opencsv.CSVReader

fun buildHashMap(csvReader: CSVReader): HashMap<LatLng, Area> {
    //val lines = csvReader.readLines()
    var nextLine: Array<String>?
    var area: Area;
    while (csvReader.readNext().also { nextLine = it } != null) {
        try {
            area =
                Area("DC", nextLine!![1].toDouble() , nextLine!![2].toDouble(), nextLine!![3].toInt())
        } catch (e: Exception) {
            //do nothing
        }

    }
    return Area.mapping
}