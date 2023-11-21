package com.packages.heatmap

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.opencsv.CSVReader
import com.packages.heatmap.ui.components.DarkThemeSwitch
import com.packages.heatmap.ui.components.Map
import com.packages.heatmap.ui.components.SearchBar
import com.packages.heatmap.ui.theme.HeatMapTheme
import com.packages.heatmap.utils.LocationViewModel
import com.packages.heatmap.walkscore.buildHashMap
import java.io.InputStreamReader


open class MainActivity : ComponentActivity() {
    private var csvFile: InputStreamReader? = null
    private var csvReader: CSVReader? = null
    private var viewModel: LocationViewModel? = null
    private var searchBar: SearchBar = SearchBar()
    private var map = Map()
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        csvFile = InputStreamReader(assets.open("HeatMap.csv"))
        csvReader = CSVReader(csvFile)
        buildHashMap(csvReader!!)
        viewModel = LocationViewModel()
        super.onCreate(savedInstanceState)
        viewModel!!.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        viewModel!!.placesClient = Places.createClient(this)
        viewModel!!.geoCoder = Geocoder(this)
        viewModel!!.firstMapObject.address = viewModel!!.geoCoder.getFromLocation(
            viewModel!!.firstMapObject.latitude,
            viewModel!!.firstMapObject.longitude,
            1
        )?.get(0)?.getAddressLine(0)

        enableEdgeToEdge()
        setContent {
            val systemTheme:Boolean = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemTheme)}
            HeatMapTheme (darkTheme = darkTheme)
            {
                HomeScreen (
                    darkTheme = darkTheme,
                    onThemeUpdated = {darkTheme = !darkTheme}
                )
            }
        }
    }
@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("PrivateResource")
@Composable
fun HomeScreen(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    /** Default Material 3 surface container color. */
    val defaultColor = MaterialTheme.colorScheme.surfaceContainer

    val color: Color = when{
        searchBar.active || map.active ->  defaultColor
        else -> Color.Transparent
    }
    window.statusBarColor = if(searchBar.active){
        defaultColor.toArgb()
    } else {
        Color.Transparent.toArgb()
    }

    window.navigationBarColor = color.toArgb()

    Surface (
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            map.ShowMap(viewModel!!, darkTheme)
            Column (
                modifier = Modifier.safeDrawingPadding()
            ) {
                Column {
                    searchBar.SearchbarField(viewModel!!, this@MainActivity, color = defaultColor)
                }
                Column (
                    modifier = Modifier.weight(1f, true),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Empty to push menu buttons to the bottom
                }
                Column {
                    DarkThemeSwitch(darkTheme, onThemeUpdated)
                }
            }
        }
    }
}



