
package com.example.nextcloud_passwords_wearos.ui.add

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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.nextcloud_passwords_wearos.ui.login.LightweightInputRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddPasswordScreen(
    onSuccess: () -> Unit,
    viewModel: AddPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()
    
    var label by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    
    LaunchedEffect(uiState) {
        if (uiState is AddPasswordUiState.Success) {
            onSuccess()
        }
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        item {
            Text("New Password")
        }
        
        item {
            LightweightInputRow(
                label = "Label",
                value = label,
                onValueChange = { label = it }
            )
        }
        item {
            LightweightInputRow(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )
        }
        item {
            LightweightInputRow(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )
        }
        item {
            LightweightInputRow(
                label = "URL",
                value = url,
                onValueChange = { url = it }
            )
        }
        
        item {
            Button(
                onClick = { viewModel.savePassword(label, username, password, url) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Save")
            }
        }
        
        if (uiState is AddPasswordUiState.Loading) {
            item {
                CircularProgressIndicator()
            }
        }
        
        if (uiState is AddPasswordUiState.Error) {
            item {
                Text(
                    text = (uiState as AddPasswordUiState.Error).message,
                    color = MaterialTheme.colors.error
                )
            }
        }
    }
}
