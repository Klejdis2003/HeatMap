package com.packages.heatmap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.opencsv.CSVReader
import com.packages.heatmap.ui.theme.HeatMapTheme
import com.packages.heatmap.ui.tools.MySearchBar
import com.packages.heatmap.walkscore.WalkScoreInformation
import com.packages.heatmap.walkscore.buildHashMap
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val csv_file = InputStreamReader(assets.open("HeatMao.csv"))
        val  bufferedReader = CSVReader(csv_file)
        super.onCreate(savedInstanceState)
        setContent {

            HeatMapTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onTertiary
                )

                {
                    Column() {

                        MySearchBar()
                        ShowMap(csvReader = bufferedReader)
                    }
                }
            }
        }
    }
}



@Composable
fun ShowMap(modifier: Modifier = Modifier, csvReader: CSVReader) {

    val dataMap: HashMap<LatLng, WalkScoreInformation> =  buildHashMap(csvReader)
    //Log.w("AppKlejdis", dataMap.toString())
    val firstMapObject: WalkScoreInformation = dataMap[dataMap.keys.first()]!!

    var location = LatLng(firstMapObject.latitude, firstMapObject.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,

    ) {
        for (key: LatLng in dataMap.keys) {
            var walkScoreObj: WalkScoreInformation = dataMap[key]!!
            location = LatLng(walkScoreObj!!.latitude, walkScoreObj.longitude)
            Marker(
                state = MarkerState(position = location),
                title =  location.toString(),
                snippet = "Walkscore: ${walkScoreObj.walkScore}"
            )
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview2() {
//    HeatMapTheme {
//        ShowMap()
//    }
//}
