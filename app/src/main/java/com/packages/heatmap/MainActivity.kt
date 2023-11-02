package com.packages.heatmap

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.opencsv.CSVReader
import com.packages.heatmap.ui.components.NavigationBar
import com.packages.heatmap.ui.components.SearchBar
import com.packages.heatmap.ui.components.ShowMap
import com.packages.heatmap.ui.theme.HeatMapTheme
import com.packages.heatmap.utils.LocationViewModel
import java.io.InputStreamReader


class MainActivity : ComponentActivity() {
    private var csvFile: InputStreamReader? = null
    private var csvReader: CSVReader? = null
    private var viewModel: LocationViewModel? = null
    private var searchBar: SearchBar? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        searchBar = SearchBar()
        csvFile = InputStreamReader(assets.open("HeatMap.csv"))
        csvReader = CSVReader(csvFile)
        viewModel = LocationViewModel(csvReader!!)
        super.onCreate(savedInstanceState)

        viewModel!!.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        viewModel!!.placesClient = Places.createClient(this)
        viewModel!!.geoCoder = Geocoder(this)

        enableEdgeToEdge()



        setContent {

            HeatMapTheme {

              HeatMapTheme {
                  HomeScreen()
              }
            }
        }
    }
    @Composable
    fun HomeScreen() {
        if(searchBar!!.active) {
            window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()

        }
        else{
            window.statusBarColor = Color.Transparent.toArgb()
        }
        HeatMapTheme {
            Surface (
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ShowMap(viewModel!!, csvReader = csvReader!!)
                Column (
                    modifier = Modifier.safeDrawingPadding()
                ) {
                    Column {
                        searchBar!!.SearchbarField(viewModel!!, this@MainActivity)
                    }
                    Column (
                        modifier = Modifier.weight(1f, true),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Empty to push menu buttons to the bottom
                    }
                    Column {
                        NavigationBar()
                    }
                }
            }
        }
    }
}


