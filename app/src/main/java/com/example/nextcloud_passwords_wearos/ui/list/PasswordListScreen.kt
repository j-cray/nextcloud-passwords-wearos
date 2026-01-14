
package com.example.nextcloud_passwords_wearos.ui.list

import android.app.Activity
import android.app.RemoteInput
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import com.example.nextcloud_passwords_wearos.data.model.Password
import org.koin.androidx.compose.koinViewModel

@Composable
fun PasswordListScreen(
    onPasswordClick: (Password) -> Unit,
    viewModel: PasswordListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()
    
    val searchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = RemoteInput.getResultsFromIntent(result.data)
            val text = results?.getCharSequence("search_result")?.toString()
            if (text != null) {
                viewModel.search(text)
            }
        }
    }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        autoCentering = AutoCenteringParams(itemIndex = 0)
    ) {
        item {
            Button(
                onClick = {
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    val remoteInputs = listOf(
                        RemoteInput.Builder("search_result")
                            .setLabel("Search Passwords")
                            .build()
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    searchLauncher.launch(intent)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text("Search")
            }
        }

        when (uiState) {
            is PasswordListUiState.Loading -> {
                item {
                    CircularProgressIndicator()
                }
            }
            is PasswordListUiState.Success -> {
                val passwords = (uiState as PasswordListUiState.Success).passwords
                if (passwords.isEmpty()) {
                    item {
                        Text(
                            text = "No passwords found",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    items(passwords) { password ->
                        Chip(
                            label = { 
                                Text(
                                    text = password.label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            secondaryLabel = { 
                                Text(
                                    text = password.username,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                ) 
                            },
                            onClick = { onPasswordClick(password) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }
                }
            }
            is PasswordListUiState.Error -> {
                item {
                    Text(
                        text = (uiState as PasswordListUiState.Error).message,
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Button(onClick = { viewModel.loadPasswords() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
