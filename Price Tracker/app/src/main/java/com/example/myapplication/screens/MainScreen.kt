package com.example.myapplication.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.utlis.ScannerUiState
import com.example.myapplication.utlis.handlePermissionResult
import com.example.myapplication.utlis.hasPermissions
import com.example.myapplication.viewmodels.ScannerViewModel

@Composable
fun MainScreen(viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { result ->
            handlePermissionResult(result, permissions, context)
        }
    val uiState by viewModel.uiState.collectAsState()

    if (hasPermissions(context, permissions)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState) {
                is ScannerUiState.Idle -> {
                    Button(onClick = { viewModel.startScanning(context) }) {
                        Text(text = "Start Scanning")
                    }
                }

                is ScannerUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Processing...")
                    }
                }

                is ScannerUiState.Success -> {
                    val scannedValue = (uiState as ScannerUiState.Success).scannedValue
                    Text(text = "Result:$scannedValue")
                }

                is ScannerUiState.Cancel -> {
                    Toast.makeText(context, "Scanning has been canceled", Toast.LENGTH_LONG).show()
                    viewModel.resetStateToIdle()
                }

                is ScannerUiState.Error -> {
                    val error = (uiState as ScannerUiState.Error).message
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.installGoogleScanner(context) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    } else {
        Toast.makeText(context, "Please grant relevant permission(s)", Toast.LENGTH_LONG).show()
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(permissions)
        viewModel.installGoogleScanner(context)
    }
}