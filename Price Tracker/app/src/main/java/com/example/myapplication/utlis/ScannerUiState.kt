package com.example.myapplication.utlis

sealed class ScannerUiState {
    data object Idle : ScannerUiState()
    data object Loading : ScannerUiState()
    data object Cancel : ScannerUiState()
    data class Success(val scannedValue : String) : ScannerUiState()
    data class Error(val message : String) : ScannerUiState()

}

