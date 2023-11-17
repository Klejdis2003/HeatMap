package com.packages.heatmap.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.packages.heatmap.utils.LocationViewModel


class SearchBar {
    var active by mutableStateOf(false)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchbarField(viewModel: LocationViewModel, context: Context, color: Color = MaterialTheme.colorScheme.background) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            var text by rememberSaveable { mutableStateOf("") }
            SearchBar(
                query = text,
                active = active,
                placeholder = {Text("Search...")},
                onActiveChange = { active = it;  },
                onSearch = { active = false; text = ""},
                onQueryChange = { text = it; viewModel.searchPlaces(it, context) },
                colors = SearchBarDefaults.colors(
                    containerColor = color,
                    dividerColor = MaterialTheme.colorScheme.primary)

                ) {
                for (item in viewModel.locationAutofill) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            //.sizeIn(minHeight = 80.dp)
                            .padding(6.dp)
                            .clickable(
                                enabled = true,
                                role = Role.Button,
                                onClick = {
                                    Log.w("Coordinates", viewModel.currentLatLong.toString())
                                    viewModel.getCoordinates(item)
                                    Log.w("Coordinates", viewModel.currentLatLong.toString())
                                    active = false
                                    text = ""
                                    viewModel.locationAutofill.clear()
                                    //Log.w("Current Location", viewModel.currentLocation.toString())
                                    Log.w("Current lat/lng", "Lat: ${viewModel.currentLatLong.latitude}, Lng: ${viewModel.currentLatLong.longitude}")
                                }
                            ),
                        colors = CardDefaults.cardColors(containerColor = color),
                    )
                    {
                        Row {
                            Icon(
                                Icons.Default.LocationOn, contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(5.dp))
                            Text(
                                item.address,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .heightIn(min = 60.dp)
                                    //.align(Alignment.CenterHorizontally)
                                    .wrapContentHeight(),
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.8.dp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}