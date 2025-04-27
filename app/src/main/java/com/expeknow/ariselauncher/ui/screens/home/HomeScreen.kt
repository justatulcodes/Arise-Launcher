package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.repository.AppRepository

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val apps = remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val repository = remember { AppRepository(context) }

    LaunchedEffect(Unit) {
        apps.value = repository.getInstalledApps().take(4)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(apps.value) { app ->
                AppItem(app = app, onClick = {
                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    context.startActivity(intent)
                })
            }
        }

        Button(
            onClick = { navController.navigate("applist") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("View All Apps")
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
                painter = BitmapPainter(app.icon.toBitmap().asImageBitmap()),
                contentDescription = app.name,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit
            )
        Text(
            text = app.name,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        )
    }
} 