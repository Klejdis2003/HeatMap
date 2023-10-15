package com.packages.heatmap

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.opencsv.CSVReader
import com.packages.heatmap.ui.theme.HeatMapTheme
import com.packages.heatmap.ui.tools.MySearchBar
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.*

import java.io.InputStreamReader
import java.util.Locale

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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMap(modifier: Modifier = Modifier, csvReader: CSVReader) {
    val context = LocalContext.current
    val geoCoder: Geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    val mapStyle: MapStyleOptions
    if(isSystemInDarkTheme()) {
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.dark_map_style)
    }
    else{
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.light_map_style)
    }
    var selectedCircle: String = " "
    val dataMap: HashMap<LatLng, Area> =  buildHashMap(csvReader)
    val firstMapObject: Area = dataMap[dataMap.keys.first()]!!
    var location = LatLng(firstMapObject.latitude, firstMapObject.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f);
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            minZoomPreference = 12f,
            mapType = MapType.NORMAL,
            mapStyleOptions = mapStyle,

            isIndoorEnabled = true
        )

    ) {
        for (key: LatLng in dataMap.keys) {
            val walkScoreObj: Area = dataMap[key]!!
            location = LatLng(walkScoreObj!!.latitude, walkScoreObj.longitude)
            Marker(
                state = MarkerState(position = location),
                title =  geoCoder.getFromLocation(walkScoreObj.latitude, walkScoreObj.longitude, 1)!![0].getAddressLine(0),
                snippet = "Walkscore: ${walkScoreObj.walkScore}",

            )


            Circle(
                clickable = true,
                center = location,
                radius = walkScoreObj.radius,
                strokeColor = Color.Transparent,
                fillColor = Area.getColorByWalkscore(walkScoreObj.walkScore),
                tag = geoCoder.getFromLocation(walkScoreObj.latitude, walkScoreObj.longitude, 1)!![0].getAddressLine(0),
//                onClick = {it: Circle-> selectedCircle}
            )


        }
        Marker(
             state = MarkerState(position = LatLng(38.87, -77.1)),
             title = "Marker",
             snippet = Area.getListOfAreasThatContainPoint(LatLng(38.908647, -77.036539)).size.toString()
        )

    }

}


