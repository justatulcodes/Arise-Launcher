package com.expeknow.ariselauncher.ui.screens.settings

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = SettingsTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
//            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .onFirstVisible( callback = {viewModel.checkLauncherStatus()})
    ) {
        // Header
        SettingsHeader(
            onBackClick = { navController.popBackStack() },
            theme = theme
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default Launcher Setting
            DefaultLauncherSection(
                isDefaultLauncher = state.isDefaultLauncher,
                onSetDefaultLauncher = { isDefault: Boolean ->
                    viewModel.onEvent(SettingsEvent.SetDefaultLauncher(isDefault))
                },
                theme = theme
            )

            // Task Completion Behavior
            TaskCompletionSection(
                hideCompletedTasks = state.hideCompletedTasks,
                onToggle = { hide: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleCompletedTasks(hide))
                },
                theme = theme
            )

            // Tunnel Vision Mode
            TunnelVisionSection(
                tunnelVisionMode = state.tunnelVisionMode,
                onToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleTunnelVision(enabled))
                },
                theme = theme
            )

            // Access Delays
            AccessDelaysSection(
                appDrawerDelay = state.appDrawerDelay,
                distractionAppsDelay = state.distractionAppsDelay,
                onAppDrawerDelayChange = { delay: Float ->
                    viewModel.onEvent(SettingsEvent.UpdateAppDrawerDelay(delay))
                },
                onDistractionDelayChange = { delay: Float ->
                    viewModel.onEvent(SettingsEvent.UpdateDistractionDelay(delay))
                },
                theme = theme
            )

            // Point System
            PointSystemSection(
                pointThreshold = state.pointThreshold,
                warningsEnabled = state.warningsEnabled,
                onThresholdChange = { threshold: Float ->
                    viewModel.onEvent(SettingsEvent.UpdatePointThreshold(threshold))
                },
                onWarningsToggle = { enabled: Boolean ->
                    viewModel.onEvent(SettingsEvent.ToggleWarnings(enabled))
                },
                theme = theme
            )

            // Essential Apps
            EssentialAppsSection(
                apps = state.apps,
                onAppToggle = { appId: String ->
                    viewModel.onEvent(SettingsEvent.ToggleAppEssential(appId))
                },
                theme = theme
            )

            // Danger Zone
            DangerZoneCard(
                onResetPoints = { viewModel.onEvent(SettingsEvent.ResetAllPoints) },
                onFactoryReset = { viewModel.onEvent(SettingsEvent.FactoryReset) }
            )

            // Footer
            SettingsFooter(theme = theme)
        }
    }
}

@Composable
private fun SettingsHeader(
    onBackClick: () -> Unit,
    theme: SettingsTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                "CONTROL CENTER",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = Color.White
            )
            Text(
                "CONFIGURE YOUR DISCIPLINE SYSTEM",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
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