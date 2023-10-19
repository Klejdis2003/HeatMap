package com.packages.heatmap

     import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.opencsv.CSVReader
import com.packages.heatmap.ui.components.SearchbarField
import com.packages.heatmap.ui.components.ShowMap
import com.packages.heatmap.ui.theme.HeatMapTheme
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        val csvFile = InputStreamReader(assets.open("HeatMap.csv"))
        val  csvReader = CSVReader(csvFile)
        super.onCreate(savedInstanceState)
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
                  HomeScreen(csvReader = csvReader)
              }
            }
        }
    }
}

@Composable
fun HomeScreen(csvReader: CSVReader) {
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
                    SearchbarField()
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
