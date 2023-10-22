package com.packages.heatmap

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.opencsv.CSVReader
import com.packages.heatmap.ui.components.SearchbarField
import com.packages.heatmap.ui.components.ShowMap
import com.packages.heatmap.ui.theme.HeatMapTheme
import com.packages.heatmap.utils.LocationViewModel
import java.io.InputStreamReader
import java.util.Properties

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<LocationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        val csvFile = InputStreamReader(assets.open("HeatMap.csv"))
        val  csvReader = CSVReader(csvFile)
        super.onCreate(savedInstanceState)

        viewModel.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        viewModel.placesClient = Places.createClient(this);
        viewModel.geoCoder = Geocoder(this);

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                0, 0
            ),
            navigationBarStyle = SystemBarStyle.light(
                0, 0
            )
        )
        setContent {

            HeatMapTheme {

              HeatMapTheme {
                  HomeScreen(csvReader = csvReader, viewModel, this)
              }
            }
        }
    }
}

@Composable
fun HomeScreen(csvReader: CSVReader, viewModel: LocationViewModel, context: Context) {
    HeatMapTheme {
        Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ShowMap(csvReader = csvReader)
            Column (
                modifier = Modifier.safeDrawingPadding()
            ) {
                Column {
                    SearchbarField(viewModel, context)
                }
                Column (
                    modifier = Modifier.weight(1f, true),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Empty to push menu buttons to the bottom
                }
                Column {
                    com.packages.heatmap.ui.components.NavigationBar()
                }
            }
        }
    }
}
