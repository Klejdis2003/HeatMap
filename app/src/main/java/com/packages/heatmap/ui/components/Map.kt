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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
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

class Map {
    private val TITLE_FONT_SIZE: TextUnit = 20.sp
    private val SUB_TITLE_FONT_SIZE: TextUnit = 14.sp
    private val CONTENT_PADDING = 13.dp

    var active by mutableStateOf(false)

    @Composable
    fun ShowMap(viewModel: LocationViewModel) {

        var location = viewModel.currentLatLong
        var currentZoom = 12f
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, currentZoom)
        }
        var currentArea by remember { mutableStateOf(CircleArea.mapping[viewModel.currentLatLong]) }
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
            val sheetState = rememberModalBottomSheetState()
            for (key: LatLng in viewModel.dataMap.keys) {
                val walkScoreObj: CircleArea = viewModel.dataMap[key]!!
                location = LatLng(walkScoreObj.latitude, walkScoreObj.longitude)
                Circle(
                    center = location,
                    radius = walkScoreObj.radius,
                    strokeColor = Color.Transparent,
                    fillColor = Area.getColorByWalkscore(walkScoreObj.walkscore),
                    tag = walkScoreObj,
                    clickable = true,
                    onClick = {
                        active = true
                        currentArea = it.tag as CircleArea
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
                    currentArea = viewModel.dataMap[viewModel.currentLatLong]
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
                            text = currentArea?.address ?: "No Data",
                            fontSize = TITLE_FONT_SIZE,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(3.dp, 20.dp)
                        )
                    }
                    if (currentArea?.walkscore != 0) {
                        Row(modifier = Modifier.padding(0.dp, CONTENT_PADDING))
                        {
                            Text(
                                "Walkscore: ",
                                fontSize = SUB_TITLE_FONT_SIZE,
                                fontWeight = FontWeight.Bold
                            )
                            Text(currentArea?.walkscore.toString())
                        }
                        Row(modifier = Modifier.padding(0.dp, CONTENT_PADDING))
                        {
                            Text(
                                "Description: ",
                                fontSize = SUB_TITLE_FONT_SIZE,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentArea?.description != null)
                                Text(currentArea?.description!!)
                        }
                    } else
                        Text("No walkscore data for this place. ", fontSize = SUB_TITLE_FONT_SIZE)
                }

            }

        }
    }
}