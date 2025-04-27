package com.expeknow.ariselauncher.ui.screens.applist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.AppRepository
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.drawable.toBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(navController: NavController) {
    val context = LocalContext.current
    val apps = remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val repository = remember { AppRepository(context) }

    LaunchedEffect(Unit) {
        apps.value = repository.getInstalledApps()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Apps") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps.value) { app ->
                AppListItem(app = app, onClick = {
                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun AppListItem(app: AppInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
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
            color = MaterialTheme.colorScheme.onBackground
        )
    }
} 