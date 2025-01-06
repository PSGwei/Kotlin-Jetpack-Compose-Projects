package com.example.myapplication.utlis

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.MainActivity
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


fun handlePermissionResult(
    result : Map<String,Boolean>,
    permissions : Array<String>,
    context : Context
){
    if (result.values.any{!it}){
        val showRationale = permissions.any{ permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity,permission)
        }
        if (showRationale){
            // First time denied
            Toast.makeText(
                context,
                "Permissions are required for future work",
                Toast.LENGTH_LONG
            ).show()
        }else{
            // Second time denied
            Toast.makeText(
                context,
                "Please Grant the Permission in the Setting",
                Toast.LENGTH_LONG
            ).show()
            //  open the "App Info" page in the device settings for a specific application.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    }
}

fun hasPermissions(context : Context,permissions: Array<String>) : Boolean{
    return permissions.any{ContextCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED}
}

suspend fun <T> Task<T>.await() : T{
    return suspendCancellableCoroutine {cont ->
        this.addOnSuccessListener { result ->
            cont.resume(result)
        }.addOnFailureListener{exception ->
            cont.resumeWithException(exception)
        }.addOnCanceledListener {
            cont.cancel()
        }
    }
}