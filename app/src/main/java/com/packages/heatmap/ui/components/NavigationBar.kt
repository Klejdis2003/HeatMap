package com.packages.heatmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Switch
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.packages.heatmap.R

@Composable
fun NavigationBar(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    /*
    Function for styling and placement of the navigation bar
     */
    FloatingActionButton(
        onClick = { onThemeUpdated() },
        Modifier.padding(10.dp)
    ) {
        Row (
            Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box (
                Modifier.padding(5.dp)
            )
            { Icon(painterResource(R.drawable.baseline_light_mode_24), null) }
            Box (
                Modifier.padding(5.dp)
            )
            { Switch(checked = darkTheme, onCheckedChange = { onThemeUpdated() }) }
            Box (
                Modifier.padding(5.dp)
            )
            { Icon(painterResource(R.drawable.baseline_dark_mode_24), null) }
        }
    }
}