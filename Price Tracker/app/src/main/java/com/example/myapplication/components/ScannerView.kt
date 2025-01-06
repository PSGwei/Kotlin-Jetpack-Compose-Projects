package com.example.myapplication.components

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.myapplication.viewmodels.ScannerViewModel

@Composable
fun ScannerView(
    viewModel: ScannerViewModel,
    onScanResult : (String) -> Unit,
){
    val context = LocalContext.current
    val activity = context as? Activity

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { ctx ->
        CodeScannerView(ctx).apply {
            // Initialize CodeScanner with the scanner view
            val codeScanner = CodeScanner(ctx, this)

            // Configure CodeScanner parameters
            autoFocusButtonColor = Color.WHITE
            isAutoFocusButtonVisible = true
            flashButtonColor = Color.WHITE
            isFlashButtonVisible = true
            frameColor = Color.WHITE
            frameCornersSize = 50 // 50dp to pixels conversion
            frameCornersRadius = 0
            setFrameAspectRatio(1f, 1f)
            frameSize = 0.75f
            frameThickness = 2 // 2dp to pixels conversion
            frameVerticalBias = 0.5f
            maskColor = Color.parseColor("#77000000")


            codeScanner.camera = CodeScanner.CAMERA_BACK  // Set camera (BACK/FONT or specific ID)
            codeScanner.formats = CodeScanner.ALL_FORMATS // Define formats (all by default)
            codeScanner.autoFocusMode = AutoFocusMode.SAFE // Auto-focus mode
            codeScanner.scanMode = ScanMode.SINGLE // Scan mode (SINGLE, CONTINUOUS, PREVIEW)
            codeScanner.isAutoFocusEnabled = true // Enable auto focus
            codeScanner.isFlashEnabled = false // Disable flash

            // Set callback for successful scans
            codeScanner.decodeCallback = DecodeCallback { result ->
                // Handle successful decode (on UI thread)
                activity?.runOnUiThread {
                    Toast.makeText(context, "Scan result: ${result.text}", Toast.LENGTH_LONG).show()
                    onScanResult(result.text)
                    viewModel.isScanning.value = false
                }
            }

            // Set callback for errors
            codeScanner.errorCallback = ErrorCallback { exception ->
                // Handle error (on UI thread)
                activity?.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Camera initialization error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.isScanning.value = false
                }
            }

            // Start scanning
                codeScanner.startPreview()

            // Ensure resources are released when the view is detached
            this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(p0: View) {
                    codeScanner.startPreview()
                }

                override fun onViewDetachedFromWindow(p0: View) {
                    codeScanner.releaseResources()
                }
            })
        }
    })

}