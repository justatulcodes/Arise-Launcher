package com.expeknow.ariselauncher.ui.screens.applist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.AppRepository

@Composable
fun AppListScreen(navController: NavController) {
    val context = LocalContext.current
    val appRepository = remember { AppRepository(context) }
    val viewModel: AppListViewModel = viewModel { AppListViewModel(appRepository, context) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = AppListTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        // Header
        AppListHeader(
            onSettingsClick = {
                viewModel.onEvent(AppListEvent.NavigateToSettings)
                navController.navigate("settings")
            },
            theme = theme
        )

        // Apps List
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(theme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = theme.textPrimary)
            }
        } else {
            AppsList(
                apps = state.apps,
                onAppClick = { app: AppInfo ->
                    viewModel.onEvent(AppListEvent.LaunchApp(app))
                },
                theme = theme
            )
        }
    }
}

@Composable
private fun AppListHeader(
    onSettingsClick: () -> Unit,
    theme: AppListTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "ALL APPS",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = theme.textPrimary
        )

        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = theme.textPrimary
            )
        }
    }
}

@Composable
private fun AppsList(
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    theme: AppListTheme
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(apps) { app ->
            AppListItem(
                app = app,
                onClick = { onAppClick(app) },
                theme = theme
            )
        }
    }
}

@Composable
private fun AppListItem(
    app: AppInfo,
    onClick: () -> Unit,
    theme: AppListTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = BitmapPainter(app.icon.toBitmap().asImageBitmap()),
            contentDescription = app.name,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyLarge,
            color = theme.textPrimary
        )
    }
}