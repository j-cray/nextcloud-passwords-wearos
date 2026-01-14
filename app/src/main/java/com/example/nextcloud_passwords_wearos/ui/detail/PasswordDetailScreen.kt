
package com.example.nextcloud_passwords_wearos.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun PasswordDetailScreen(
    passwordId: String,
    viewModel: PasswordDetailViewModel = koinViewModel()
) {
    LaunchedEffect(passwordId) {
        viewModel.loadPassword(passwordId)
    }
    
    val password by viewModel.password.collectAsState()
    val listState = rememberScalingLazyListState()
    var showPassword by remember { mutableStateOf(false) }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        val p = password
        if (p != null) {
            item {
                Text(
                    text = p.label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.title3,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                TitleCard(
                    onClick = { /* Copy? */ },
                    title = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(p.username)
                }
            }
            
            item {
                TitleCard(
                    onClick = { showPassword = !showPassword },
                    title = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (showPassword) p.password else "••••••••")
                }
            }
            
            if (!p.url.isNullOrEmpty()) {
                item {
                    TitleCard(
                        onClick = { /* Open on phone? */ },
                        title = { Text("URL") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(p.url)
                    }
                }
            }
        } else {
            item {
                Text("Password not found")
            }
        }
    }
}
