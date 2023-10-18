package com.packages.heatmap.ui.components

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchbarField() {
    /*
    Function for styling and placement of the searchbar
     */
    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        var text by rememberSaveable { mutableStateOf("Enter Query") }
        var active by rememberSaveable { mutableStateOf(false) }
        SearchBar(
            query = text,
            active = active,
            onActiveChange = {active = it},
            onSearch = {active = false},
            onQueryChange = {text = it}

        ) {
            Text("Suggestions")
        }
    }
}