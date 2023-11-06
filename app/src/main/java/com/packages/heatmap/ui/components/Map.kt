package com.packages.heatmap.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
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
import com.packages.heatmap.R
import com.packages.heatmap.utils.LocationViewModel
import com.packages.heatmap.walkscore.Area
import com.packages.heatmap.walkscore.CircleArea
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FlowOperatorInvokedInComposition")

class Map() {
    var active by mutableStateOf(false)
    @Composable
    fun ShowMap(viewModel: LocationViewModel, csvReader: CSVReader) {

        var location = viewModel.currentLatLong
        var currentZoom: Float = 12f
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, currentZoom)
        }
        var markerPosition: LatLng = viewModel.currentLatLong
        LaunchedEffect(cameraPositionState) {
            snapshotFlow { viewModel.currentLatLong }.collectLatest {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(
                        viewModel.currentLatLong
                    ), durationMs = 700
                )
                currentZoom = 12f
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
                mapType = MapType.NORMAL,
                mapStyleOptions = mapStyle,
                isIndoorEnabled = true
            ),
            onMapLongClick = {
                currentZoom = 12f
                viewModel.currentLatLong = LatLng(it.latitude, it.longitude)
                viewModel.update()
            }
        )
        {
            val currentArea = CircleArea.mapping[viewModel.currentLatLong]
            val context = LocalContext.current
            val sheetState = rememberModalBottomSheetState()
            for (key: LatLng in viewModel.dataMap.keys) {
                val walkScoreObj: CircleArea = viewModel.dataMap[key]!!
                location = LatLng(walkScoreObj.latitude, walkScoreObj.longitude)
                Circle(
                    center = location,
                    radius = walkScoreObj.radius,
                    strokeColor = Color.Transparent,
                    fillColor = Area.getColorByWalkscore(walkScoreObj.walkscore),
                    onClick = {
                        active = true
                    }
                )
            }

            Marker(
                state = MarkerState(position = viewModel.currentLatLong),
                title = CircleArea.mapping[viewModel.currentLatLong]?.address,
                snippet = when (currentArea?.walkscore) {
                    0 -> "Walkscore: No Data"
                    else -> "Walkscore: ${currentArea?.walkscore}"
                },
                onClick = {
                    active = true
                    true
                }
            )
            if (active) {
                ModalBottomSheet(
                    onDismissRequest = { active = false },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    sheetState = sheetState,
                    tonalElevation = SearchBarDefaults.TonalElevation
                )
                {
                    Row()
                    {
                        Text(
                            text = currentArea?.address!!,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(3.dp, 20.dp)
                        )
                    }
                    Row(modifier = Modifier.padding(0.dp, 13.dp))
                    {
                        Text("Walkscore: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(currentArea?.walkscore.toString())
                    }
                    Row(modifier = Modifier.padding(0.dp, 13.dp))
                    {
                        Text("Description: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(currentArea?.description!!)
                    }
                }

            }

        }
    }
}