package com.expeknow.ariselauncher.ui.screens.onboarding

import android.app.Activity
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Layers
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

enum class PermissionType {
    OVERLAY,
    USAGE_STATS
}

@Composable
fun PermissionOnboardingScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var overlayGranted by remember { mutableStateOf(PermissionHelper.hasOverlayPermission(context)) }
    var usageStatsGranted by remember { mutableStateOf(PermissionHelper.hasUsageStatsPermission(context)) }
    var currentPermissionRequest by remember { mutableStateOf<PermissionType?>(null) }

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val callback = object : android.app.Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                overlayGranted = PermissionHelper.hasOverlayPermission(context)
                usageStatsGranted = PermissionHelper.hasUsageStatsPermission(context)

                if (overlayGranted && usageStatsGranted) {
                    val prefs = context.getSharedPreferences("arise_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit { putBoolean("has_seen_welcome", true) }

                    navController.navigate(com.expeknow.ariselauncher.ui.navigation.Screen.Focus.route) {
                        popUpTo(com.expeknow.ariselauncher.ui.navigation.Screen.PermissionOnboarding.route) {
                            inclusive = true
                        }
                    }
                }
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        }

        activity?.application?.registerActivityLifecycleCallbacks(callback)

        onDispose {
            activity?.application?.unregisterActivityLifecycleCallbacks(callback)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Title
            Text(
                text = "Welcome to Arise",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "To provide you with the best experience, we need a couple of permissions",
                fontSize = 16.sp,
                color = BannerTextGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            PermissionCard(
                icon = Icons.Outlined.Layers,
                title = "Display Over Other Apps",
                description = "Allows Arise to show floating timers and notifications on top of other apps, helping you stay focused and track your productivity in real-time.",
                isGranted = overlayGranted,
                onGrantClick = {
                    currentPermissionRequest = PermissionType.OVERLAY
                    PermissionHelper.requestOverlayPermission(context)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionCard(
                icon = Icons.Outlined.Analytics,
                title = "Usage Access",
                description = "Enables Arise to track your app usage and screen time, providing insights into your habits and helping you achieve your productivity goals.",
                isGranted = usageStatsGranted,
                onGrantClick = {
                    currentPermissionRequest = PermissionType.USAGE_STATS
                    PermissionHelper.requestUsageStatsPermission(context)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Both permissions are required to use Arise. Your privacy is important to us - all data stays on your device.",
                fontSize = 13.sp,
                color = BannerTextGray.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
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
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentGreen,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TaskTitle,
                    modifier = Modifier.weight(1f)
                )

                if (isGranted) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Granted",
                        tint = AccentGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = BannerTextGray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGrantClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isGranted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGranted) AccentGreen.copy(alpha = 0.3f) else AccentGreen,
                    contentColor = if (isGranted) Color.White.copy(alpha = 0.5f) else Color.Black,
                    disabledContainerColor = AccentGreen.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (isGranted) "Granted" else "Grant Permission",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
