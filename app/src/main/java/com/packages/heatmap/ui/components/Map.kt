package com.packages.heatmap.ui.components

import android.annotation.SuppressLint
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
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
import com.packages.heatmap.utils.LocationViewModel
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.CircleArea
import com.packages.heatmap.walkscore.buildHashMap
import kotlinx.coroutines.flow.onStart


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun ShowMap(viewModel: LocationViewModel, csvReader: CSVReader) {
    val mapStyle: MapStyleOptions

    val dataMap: HashMap<LatLng, CircleArea> = buildHashMap(csvReader)
    val firstMapObject: Area = dataMap[dataMap.keys.first()]!!
    var location = viewModel.currentLatLong
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { viewModel.currentLatLong }.collect {
            cameraPositionState.move(CameraUpdateFactory.newLatLng(viewModel.currentLatLong))
        }
}


    if (isSystemInDarkTheme()) {
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.dark_map_style)
    } else {
        mapStyle = MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.light_map_style)
    }
    Box(modifier = Modifier.fillMaxSize())
    {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                minZoomPreference = 12f,
                mapType = MapType.NORMAL,
                mapStyleOptions = mapStyle,

                isIndoorEnabled = true
            )

        )
        {
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
                    center = location,
                    radius = walkScoreObj.radius,
                    strokeColor = Color.Transparent,
                    fillColor = Area.getColorByWalkscore(walkScoreObj.walkScore),
                    tag = walkScoreObj,
//                onClick = {it: Circle-> selectedCircle}
                )

            }
        }

        Button(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
            cameraPositionState.move(CameraUpdateFactory.newLatLng(viewModel.currentLatLong))
        })
        {

        }
    }

}
@Composable
fun InsertNewMarkerFromSearch(placesClient : PlacesClient, placeId : String){
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