package com.expeknow.ariselauncher.ui.screens.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onFirstVisible
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.theme.BannerTextGray

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = SettingsTheme()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkLauncherStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SettingsHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DefaultLauncherSection(
                isDefaultLauncher = state.isDefaultLauncher,
                onSetDefaultLauncher = { isDefault: Boolean ->
                    viewModel.onEvent(SettingsEvent.SetDefaultLauncher(isDefault))
                },
                theme = theme
            )

            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))

            TunnelVisionSection(
                tunnelVisionMode = state.tunnelVisionMode,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleTunnelVision(enabled))
                },
                theme = theme
            )

            ShowWeeklyScheduleSection(
                isShowWeeklyScheduleEnabled = state.showWeeklyScheduleEnabled,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleWeeklySchedule(enabled))
                },
                theme = theme
            )

            ShowHomeScreenSection(
                showHomeScreen = state.showHomeScreen,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleShowHomeScreen(enabled))
                },
                theme = theme
            )

            AppTimerSection(
                appTimerEnabled = state.appTimerEnabled,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleAppTimer(enabled))
                },
                theme = theme
            )

            TaskCompletionSection(
                hideCompletedTasks = state.hideCompletedTasks,
                onToggle = { hide: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleCompletedTasks(hide))
                },
                theme = theme
            )

            KeyboardTriggerSection(
                keyboardTriggerEnabled = state.keyboardTriggerEnabled,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleKeyboardTrigger(enabled))
                },
                theme = theme
            )

            ShowCategorizedApps(
                categorizedAppsEnabled = state.showCategorizedAppsEnabled,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleShowCategorizedApps(enabled))
                },
                theme = theme
            )

            AppLaunchPopupSection(
                appLaunchPopupEnabled = state.appLaunchPopupEnabled,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleAppLaunchPopup(enabled))
                },
                theme = theme
            )

            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))


            DangerZoneCard(
                onShowRefreshAppDrawerDialog = { viewModel.onEvent(SettingsEvent.ShowAppRefreshDialog) },
                onShowResetPointsDialog = { viewModel.onEvent(SettingsEvent.ShowResetPointsDialog) },
                onShowFactoryResetDialog = { viewModel.onEvent(SettingsEvent.ShowFactoryResetDialog) },
            )

            SupportSection(
                onClick = { navController.navigate(Screen.Support.route) },
                theme = theme
            )

            SettingsFooter(theme = theme)
        }
    }

    // Confirmation Dialogs
    if (state.showResetPointsDialog) {
        ConfirmationDialog(
            title = "RESET ALL POINTS",
            message = "This action will permanently reset all points for all apps. This cannot be undone.",
            onConfirm = { viewModel.onEvent(SettingsEvent.ResetAllPoints) },
            onDismiss = { viewModel.onEvent(SettingsEvent.HideResetPointsDialog) },
            theme = theme,
            isDestructive = true
        )
    }

    if (state.showFactoryResetDialog) {
        ConfirmationDialog(
            title = "FACTORY RESET",
            message = "This will delete all tasks, reset all points, and restore settings to default values. This action cannot be undone.",
            onConfirm = { viewModel.onEvent(SettingsEvent.FactoryReset) },
            onDismiss = { viewModel.onEvent(SettingsEvent.HideFactoryResetDialog) },
            theme = theme,
            isDestructive = true
        )
    }

    if(state.showAppRefreshDialog) {
        ConfirmationDialog(
            title = "REFRESH APP DRAWER",
            message = "This will refresh the app drawer to reflect any changes made to your device's installed apps." +
                    " This action is safe and can be done at any time.",
            onConfirm = { viewModel.onEvent(SettingsEvent.RefreshApps) },
            onDismiss = { viewModel.onEvent(SettingsEvent.HideAppRefreshDialog) },
            theme = theme,
            isDestructive = false
        )
    }
}

@Composable
private fun SettingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Control Center",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                "Configure your discipline system",
                style = MaterialTheme.typography.bodyMedium,
                color = BannerTextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

        }
    }
}

@Composable
private fun SettingsFooter(theme: SettingsTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "DISCIPLINE IS THE BRIDGE BETWEEN GOALS AND ACCOMPLISHMENT",
            style = MaterialTheme.typography.labelSmall,
            color = theme.accent.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        navController = rememberNavController(),
        viewModel = viewModel()
    )
}