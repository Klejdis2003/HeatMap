package com.packages.heatmap.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.packages.heatmap.R

@Composable
fun NavigationBar(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    /*
    Function for styling and placement of the navigation bar
     */
    NavigationBar (
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(selected = false,
            onClick = { onThemeUpdated() },
            icon = { Icon(painterResource(if (darkTheme) R.drawable.baseline_dark_mode_24
            else R.drawable.baseline_light_mode_24), null) },
            label = { if (darkTheme) Text("Dark Mode") else Text(text = "Light Mode") }
        )

    }
}