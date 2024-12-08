package com.example.myapplication.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.utlis.ScannerUiState
import com.example.myapplication.utlis.await
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel : ViewModel() {
    val isScanning = mutableStateOf(false)

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Idle)
    val uiState = _uiState


    fun installGoogleScanner(context: Context){
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Loading
            try {
                val isInstalled = installGoogleScannerSuspend(context)
                if (isInstalled){
                    _uiState.value = ScannerUiState.Idle
                }else{
                    _uiState.value = ScannerUiState.Error("Failed to install scanner module")
                }
            }catch (e:Exception){
                _uiState.value = ScannerUiState.Error(e.message ?: "Unknown error occur")
            }

        }
    }

    fun startScanning(context: Context){
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Loading
            try {
                val scannedValue = performScan(context)
                _uiState.value = ScannerUiState.Success(scannedValue)
            }catch (e : CancellationException){
                _uiState.value = ScannerUiState.Cancel
            }catch (e: Exception){
                _uiState.value = ScannerUiState.Error(e.message ?: "Scan Failed")
            }
        }
    }

    private suspend fun installGoogleScannerSuspend(context : Context) : Boolean{
        val moduleInstallClient = ModuleInstall.getClient(context)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(context))
            .build()
        return moduleInstallClient.installModules(moduleInstallRequest).await().areModulesAlreadyInstalled()
    }

    private suspend fun performScan(context: Context) : String{
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13)
            .enableAutoZoom()
            .build()

        val scanner = GmsBarcodeScanning.getClient(context,options)
        val barcode = scanner.startScan().await()
        return barcode.rawValue ?: throw Exception("No barcode value found.")
    }

    fun resetStateToIdle(){
        _uiState.value = ScannerUiState.Idle
    }


}