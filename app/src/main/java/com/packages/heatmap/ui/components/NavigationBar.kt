package com.packages.heatmap.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.packages.heatmap.R

@Composable
fun NavigationBar(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    /*
    Function for styling and placement of the navigation bar
     */
    Row {
        Row(
            Modifier.weight(1f, true)
        ) {
            //To space the switch to the right
        }
        FloatingActionButton(
            onClick = { onThemeUpdated() },
            Modifier.padding(10.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Box(
//                    Modifier.padding(5.dp)
//                )
//                { Icon(painterResource(R.drawable.baseline_light_mode_24), null) }
                Box(
                    Modifier
                        .padding(3.dp, 10.dp)
                        .rotate(90f)
                )
                {
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { onThemeUpdated() },
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.inversePrimary,
                            uncheckedIconColor = MaterialTheme.colorScheme.primary
                        ),
                        thumbContent = {
                            Box (
                                Modifier.rotate(-90f)
                            ) {
                                if (darkTheme) {
                                    Icon(painterResource(R.drawable.baseline_dark_mode_24), null)
                                } else {

                                    Icon(painterResource(R.drawable.baseline_light_mode_24), null)
                                }
                            }
                        }
                    )
                }
//                Box(
//                    Modifier.padding(5.dp)
//                )
//                { Icon(painterResource(R.drawable.baseline_dark_mode_24), null) }
            }
        }
    }
}