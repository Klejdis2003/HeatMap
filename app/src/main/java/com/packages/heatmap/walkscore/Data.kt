package com.packages.heatmap.walkscore

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.opencsv.CSVReader
import java.io.BufferedReader

fun buildHashMap(csvReader: CSVReader): HashMap<LatLng, WalkScoreInformation> {
    //val lines = csvReader.readLines()
    var nextLine: Array<String>?
    var walkScoreInformation: WalkScoreInformation;
    while (csvReader.readNext().also { nextLine = it } != null) {
        try {
            walkScoreInformation =
                WalkScoreInformation("DC", nextLine!![1].toDouble() , nextLine!![2].toDouble(), nextLine!![3].toInt())
        } catch (e: Exception) {
            //do nothing
        }

    }
    return WalkScoreInformation.mapping
}