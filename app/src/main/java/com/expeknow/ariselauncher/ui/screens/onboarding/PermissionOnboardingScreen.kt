package com.expeknow.ariselauncher.ui.screens.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.expeknow.ariselauncher.ui.theme.AccentGreen
import com.expeknow.ariselauncher.ui.theme.SurfaceCard
import com.expeknow.ariselauncher.ui.theme.TaskTitle
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.utils.PermissionHelper
import androidx.core.content.edit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionOnboardingScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var overlayGranted by remember { mutableStateOf(PermissionHelper.hasOverlayPermission(context)) }
    var usageStatsGranted by remember { mutableStateOf(PermissionHelper.hasUsageStatsPermission(context)) }

    // Helper function to check notification permission
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed for older versions
        }
    }

    var notificationGranted by remember { mutableStateOf(hasNotificationPermission()) }

    // Notification permission state (only for Android 13+)
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    // Check permissions whenever the composable resumes or notification permission state changes
    LaunchedEffect(notificationPermissionState?.status) {
        overlayGranted = PermissionHelper.hasOverlayPermission(context)
        usageStatsGranted = PermissionHelper.hasUsageStatsPermission(context)
        notificationGranted = hasNotificationPermission()
    }

    // Re-check permissions when returning from settings
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                overlayGranted = PermissionHelper.hasOverlayPermission(context)
                usageStatsGranted = PermissionHelper.hasUsageStatsPermission(context)
                notificationGranted = hasNotificationPermission()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Function to navigate to the next screen
    fun navigateToNextScreen() {
        val prefs = context.getSharedPreferences("arise_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit { putBoolean("has_seen_welcome", true) }

        navController.navigate(com.expeknow.ariselauncher.ui.navigation.Screen.Focus.route) {
            popUpTo(com.expeknow.ariselauncher.ui.navigation.Screen.PermissionOnboarding.route) {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                 .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Welcome to Arise",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "To provide you with the best experience, we need a few permissions",
                fontSize = 14.sp,
                color = BannerTextGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            PermissionCard(
                icon = Icons.Outlined.Layers,
                title = "Display Over Other Apps",
                description = "Allows Arise to show floating timers and notifications on top of other apps, helping you stay focused and track your productivity in real-time.",
                isGranted = overlayGranted,
                onGrantClick = {
                    PermissionHelper.requestOverlayPermission(context)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            PermissionCard(
                icon = Icons.Outlined.Analytics,
                title = "Usage Access",
                description = "Enables Arise to track your app usage and screen time, providing insights into your habits and helping you achieve your productivity goals.",
                isGranted = usageStatsGranted,
                onGrantClick = {
                    PermissionHelper.requestUsageStatsPermission(context)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Permission Card (only show on Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionCard(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    description = "Allows Arise to send you task reminders and progress updates throughout the day, helping you stay on track with your goals.",
                    isGranted = notificationGranted,
                    onGrantClick = {
                        notificationPermissionState?.launchPermissionRequest()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "These permissions help Arise provide the best experience, but you can continue without them. Your privacy is important to us - all data stays on your device.",
                fontSize = 12.sp,
                color = BannerTextGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Continue button - Primary action with green background
            Button(
                onClick = { navigateToNextScreen() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (overlayGranted && usageStatsGranted && notificationGranted) {
                        "Continue"
                    } else {
                        "Continue Anyway"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceCard
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaskTitle,
                    modifier = Modifier.weight(1f)
                )

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Granted",
                        tint = AccentGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = BannerTextGray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Outlined button style for secondary action
            OutlinedButton(
                onClick = onGrantClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGranted,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AccentGreen,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = AccentGreen.copy(alpha = 0.5f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isGranted) AccentGreen.copy(alpha = 0.3f) else AccentGreen
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    text = if (isGranted) "Granted" else "Grant Permission",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
