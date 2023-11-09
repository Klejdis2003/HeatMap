package com.packages.heatmap.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
        var currentZoom by remember { mutableFloatStateOf(12f) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, currentZoom)
        }
        var currentArea by remember { mutableStateOf(CircleArea.mapping[viewModel.currentLatLong]) }
        LaunchedEffect(cameraPositionState) {
            snapshotFlow { viewModel.currentLatLong }.collectLatest {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        viewModel.currentLatLong,
                        currentZoom
                    ), durationMs = 700
                )
                currentZoom = 10f
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
            uiSettings = MapUiSettings(compassEnabled = false, zoomControlsEnabled = false, mapToolbarEnabled = true),
            onMapLongClick = {
                currentZoom = when {
                    cameraPositionState.position.zoom < 16f -> 16f
                    else -> cameraPositionState.position.zoom
                }
                viewModel.currentLatLong = LatLng(it.latitude, it.longitude)
                viewModel.update()
            }
        )
        {

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
                        val obj = it.tag as CircleArea
                        if (obj.address == "")
                            obj.address =
                                viewModel.geoCoder.getFromLocation(obj.latitude, obj.longitude, 1)
                                    ?.get(0)?.getAddressLine(0)!!
                        active = true
                        currentArea = it.tag as CircleArea
                    }
                )
            }
            Marker(
                state = MarkerState(position = viewModel.currentLatLong),
                onClick = {
                    currentArea = viewModel.dataMap[viewModel.currentLatLong]
                    active = true
                    true
                }
            )
            if (active)
                ShowBottomSheet(currentArea!!)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShowBottomSheet(currentArea: CircleArea) {
        val context = LocalContext.current
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { active = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            sheetState = sheetState,
            tonalElevation = SearchBarDefaults.TonalElevation,
            modifier = Modifier.defaultMinSize()
        )
        {
            Row(modifier = Modifier.wrapContentSize().padding(12.dp, 0.dp))
            {
                Text(
                    text = currentArea.address ?: "No Data",
                    fontSize = TITLE_FONT_SIZE,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(3.dp, 20.dp)
                )
            }

            if (currentArea.walkscore != 0) {
                Row(modifier = Modifier.wrapContentSize().padding(12.dp, 4.dp)) {
                    Column {
                        Row(modifier = Modifier.padding(0.dp, CONTENT_PADDING))
                        {
                            Text(
                                "Walkscore: ",
                                fontSize = SUB_TITLE_FONT_SIZE,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${currentArea?.walkscore.toString()}/100")
                        }
                        Row(
                            modifier = Modifier.padding(0.dp, CONTENT_PADDING)
                                .wrapContentSize()
                        )
                        {
                            Text(
                                "Description: ",
                                fontSize = SUB_TITLE_FONT_SIZE,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentArea.description != null)
                                Text(currentArea.description!!)
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Bottom)
                            .padding(8.dp, CONTENT_PADDING)
                    )
                    {
                        FloatingActionButton(
                            content = {
                                Icon(
                                    Icons.Outlined.Place,
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            },
                            onClick = {
                                val uri =
                                    "google.navigation:q=${currentArea.latitude},${currentArea.longitude}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                intent.setPackage("com.google.android.apps.maps")
                                context.startActivity(intent)
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = ButtonDefaults.shape
                        )
                    }
                }

            } else
                Text("No walkscore data for this place. ", fontSize = SUB_TITLE_FONT_SIZE)

        }
    }
}