package com.example.myapplication.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val isScanning = mutableStateOf(false)
    val scannerResult = mutableStateOf("")
}