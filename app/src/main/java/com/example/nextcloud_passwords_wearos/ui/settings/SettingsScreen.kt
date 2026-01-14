
package com.example.nextcloud_passwords_wearos.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    val theme by viewModel.theme.collectAsState()
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        item {
            Text("Settings")
        }
        
        item {
            Text("Theme")
        }
        
        item {
            ToggleChip(
                checked = theme == "system",
                onCheckedChange = { if (it) viewModel.setTheme("system") },
                label = { Text("System") },
                toggleControl = {
                    if (theme == "system") {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ToggleChip(
                checked = theme == "dark",
                onCheckedChange = { if (it) viewModel.setTheme("dark") },
                label = { Text("Dark") },
                toggleControl = {
                    if (theme == "dark") {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ToggleChip(
                checked = theme == "light",
                onCheckedChange = { if (it) viewModel.setTheme("light") },
                label = { Text("Light") },
                toggleControl = {
                    if (theme == "light") {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            Chip(
                label = { Text("Logout") },
                onClick = { 
                    viewModel.logout()
                    onLogout()
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item {
            Text("Version 1.0")
        }
    }
}
