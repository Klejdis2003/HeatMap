package com.packages.heatmap.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.packages.heatmap.utils.LocationViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.packages.heatmap.MainActivity



class SearchBar {
    var active by mutableStateOf(false)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchbarField(viewModel: LocationViewModel, context: Context) {
        /*
        Function for styling and placement of the searchbar
         */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val defaultText = "Search..."
            var text by rememberSaveable { mutableStateOf("Search...") }
            SearchBar(
                query = text,
                active = active,
                onActiveChange = { active = it; text = "";  },
                onSearch = { active = false; text = defaultText },
                onQueryChange = { text = it; viewModel.searchPlaces(it, context) },

                ) {
                for (item in viewModel.locationAutofill) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(minHeight = 80.dp)
                            .padding(6.dp)
                            .clickable(
                                enabled = true,
                                role = Role.Button,
                                onClick = {
                                    viewModel.getCoordinates(item)
                                    Log.w("Coordinates", viewModel.currentLatLong.toString())
                                    active = false
                                    text = defaultText
                                    viewModel.currentLocationAddress = item.address
                                    viewModel.locationAutofill.clear()
                                }
                            ),
                        border = BorderStroke(1.dp, Color.Black),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    )
                    {
                        Text(
                            item.address,
                            modifier = Modifier.padding(5.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                }
            }
        }
    }
}