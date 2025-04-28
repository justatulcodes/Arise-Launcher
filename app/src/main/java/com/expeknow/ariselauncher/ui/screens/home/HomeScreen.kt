package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.expeknow.ariselauncher.data.model.AppInfo
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.repository.AppRepository
import com.expeknow.ariselauncher.data.repository.TaskRepository
import com.expeknow.ariselauncher.ui.components.TaskDialog
import com.expeknow.ariselauncher.ui.components.TaskItem

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val apps = remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val appRepository = remember { AppRepository(context) }
    val taskRepository = remember { TaskRepository() }
    val tasks = remember { mutableStateOf<List<Task>>(emptyList()) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        apps.value = appRepository.getInstalledApps().take(4)
    }
    
    LaunchedEffect(taskRepository) {
        tasks.value = taskRepository.getAllTasks()
    }

    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tasks section
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tasks.value.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No tasks yet. Add a task to get started!",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        items(tasks.value) { task ->
                            TaskItem(
                                task = task,
                                onTaskCompleted = { taskId ->
                                    taskRepository.completeTask(taskId)
                                    tasks.value = taskRepository.getAllTasks()
                                }
                            )
                        }
                    }
                }

                // App icons at the bottom
                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        apps.value.forEach { app ->
                            AppItem(app = app, onClick = {
                                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                context.startActivity(intent)
                            })
                        }
                    }

                    Button(
                        onClick = { navController.navigate("applist") },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("View All Apps")
                    }
                }
            }

            // Add task floating action button
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    }

    if (showAddTaskDialog) {
        TaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onTaskAdded = { title, description ->
                taskRepository.addTask(title, description)
                tasks.value = taskRepository.getAllTasks()
            }
        )
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
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )
    }
}