package com.expeknow.ariselauncher.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not needed for Android 12 and below
            true
        }
    }

    fun shouldRequestNotificationPermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !isNotificationPermissionGranted(context)
    }

    // For Compose UI: Use Accompanist Permissions library
    // Example usage in your Composable:
    // val notificationPermissionState = rememberPermissionState(
    //     permission = Manifest.permission.POST_NOTIFICATIONS
    // )
    //
    // if (NotificationPermissionHelper.shouldRequestNotificationPermission(context)) {
    //     LaunchedEffect(Unit) {
    //         notificationPermissionState.launchPermissionRequest()
    //     }
    // }
}

