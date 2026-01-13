
package com.example.nextcloud_passwords_wearos.ui.login

import android.app.Activity
import android.app.RemoteInput
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import org.koin.androidx.compose.koinViewModel
import java.util.Collections

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Use standard Column with verticalScroll for better performance than ScalingLazyColumn
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 32.dp), // Add padding to avoid cutting off on round screens
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nextcloud Passwords",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (uiState) {
            is LoginUiState.Idle -> {
                WearInputChip(
                    label = "Server URL",
                    value = serverUrl,
                    onValueChange = { serverUrl = it }
                )
                WearInputChip(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it }
                )
                WearInputChip(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )
                Button(
                    onClick = { viewModel.login(serverUrl, username, password) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Login")
                }
            }
            is LoginUiState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginUiState.Success -> {
                Text("Logged in")
                Text(
                    text = "Autofill service is ready.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
            is LoginUiState.Error -> {
                Text(
                    text = "Error: ${(uiState as LoginUiState.Error).message}",
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { viewModel.login(serverUrl, username, password) }, // Retry
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun WearInputChip(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = RemoteInput.getResultsFromIntent(result.data)
            val text = results?.getCharSequence("input_result")?.toString()
            if (text != null) {
                onValueChange(text)
            }
        }
    }

    Chip(
        label = { Text(label) },
        secondaryLabel = { 
            Text(
                if (value.isEmpty()) "Tap to enter" 
                else if (isPassword) "•".repeat(value.length) 
                else value
            ) 
        },
        onClick = {
            val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
            val remoteInputs = listOf(
                RemoteInput.Builder("input_result")
                    .setLabel(label)
                    .build()
            )
            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
            launcher.launch(intent)
        },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}
