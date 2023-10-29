package com.packages.heatmap.ui.components

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import com.opencsv.CSVReader
import com.packages.heatmap.R
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.CircleArea
import com.packages.heatmap.walkscore.buildHashMap


@Composable
fun ShowMap(csvReader: CSVReader) {
    val mapStyle: MapStyleOptions

    val dataMap: HashMap<LatLng, CircleArea> =  buildHashMap(csvReader)
    val firstMapObject: Area = dataMap[dataMap.keys.first()]!!
    var location = LatLng(firstMapObject.latitude, firstMapObject.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }
    if(isSystemInDarkTheme()) {
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.dark_map_style)
    }
    else{
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.light_map_style)
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
            val walkScoreObj: CircleArea = dataMap[key]!!
            location = LatLng(walkScoreObj.latitude, walkScoreObj.longitude)
//            Marker(
//                state = MarkerState(position = location),
//                title =  geoCoder.getFromLocation(walkScoreObj.latitude, walkScoreObj.longitude, 1)!![0].getAddressLine(0),
//                snippet = "Walkscore: ${walkScoreObj.walkScore}",
//
//            )

            Circle(
                clickable = true,
                center = location,
                radius = walkScoreObj.radius,
                strokeColor = Color.Transparent,
                fillColor = Area.getColorByWalkscore(walkScoreObj.walkScore),
                tag = walkScoreObj,
//                onClick = {it: Circle-> selectedCircle}
            )
        }

    }
}
@Composable
fun insertNewMarkerFromSearch(placesClient : PlacesClient, placeId : String){
    val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)
    placesClient.fetchPlace(request)
        .addOnSuccessListener { response: FetchPlaceResponse ->
            val place = response.place
        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                TODO("Handle error with given status code")
            }
        }
    Log.w("Klejd", placeFields.toString())
}