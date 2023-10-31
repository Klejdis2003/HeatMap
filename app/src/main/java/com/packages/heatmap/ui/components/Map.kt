package com.packages.heatmap.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
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


@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun ShowMap(viewModel: LocationViewModel, csvReader: CSVReader) {
    var location = viewModel.currentLatLong
    val dataMap = viewModel.dataMap
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 12f)
    }
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { viewModel.currentLatLong }.collect {
            cameraPositionState.animate(update = CameraUpdateFactory.newLatLng(viewModel.currentLatLong), durationMs = 700)
        }
    }
    val mapStyle = if (isSystemInDarkTheme()) {
        MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.dark_map_style)
    } else {
        MapStyleOptions.loadRawResourceStyle(LocalContext.current, R.raw.light_map_style)
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

        )
    {
        for (key: LatLng in dataMap.keys) {
            val walkScoreObj: CircleArea = dataMap[key]!!
            location = LatLng(walkScoreObj.latitude, walkScoreObj.longitude)

            Circle(
                center = location,
                radius = walkScoreObj.radius,
                strokeColor = Color.Transparent,
                fillColor = Area.getColorByWalkscore(walkScoreObj.walkScore),
                tag = walkScoreObj,
            )

        }
    }
}