
package com.example.nextcloud_passwords_wearos.ui.login

import android.app.Activity
import android.app.RemoteInput
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showManualLogin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nextcloud Passwords",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp),
            style = MaterialTheme.typography.title3
        )

        when (uiState) {
            is LoginUiState.Idle -> {
                if (!showManualLogin) {
                    Text(
                        text = "Scan to Login",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { viewModel.showQrCode() },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text("Show QR Code")
                    }
                    Button(
                        onClick = { showManualLogin = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manual Login")
                    }
                } else {
                    ManualLoginForm(viewModel)
                }
            }
            is LoginUiState.ShowQr -> {
                val qrContent = (uiState as LoginUiState.ShowQr).qrContent
                QrCodeImage(content = qrContent)
                Text(
                    text = "Scan with phone app",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption2,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = { viewModel.logout() }, // Back to Idle (logout resets state)
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Cancel")
                }
            }
            is LoginUiState.Loading -> {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            is LoginUiState.Success -> {
                Text("Logged in")
                Text(
                    text = "Autofill ready",
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
                    onClick = { viewModel.showQrCode() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Retry QR")
                }
                Button(
                    onClick = { showManualLogin = true },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Manual Login")
                }
            }
        }
    }
}

@Composable
fun QrCodeImage(content: String) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(content) {
        val width = 300
        val height = 300
        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height
            )
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap = bmp
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
fun ManualLoginForm(viewModel: LoginViewModel) {
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LightweightInputRow(
        label = "Server URL",
        value = serverUrl,
        onValueChange = { serverUrl = it }
    )
    LightweightInputRow(
        label = "Username",
        value = username,
        onValueChange = { username = it }
    )
    LightweightInputRow(
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

@Composable
fun LightweightInputRow(
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.surface)
            .clickable {
                val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                val remoteInputs = listOf(
                    RemoteInput.Builder("input_result")
                        .setLabel(label)
                        .build()
                )
                RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                launcher.launch(intent)
            }
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.caption2,
                color = MaterialTheme.colors.onSurfaceVariant
            )
            Text(
                text = if (value.isEmpty()) "Tap to enter" 
                       else if (isPassword) "•".repeat(value.length) 
                       else value,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}
