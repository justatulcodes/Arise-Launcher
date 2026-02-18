package com.expeknow.ariselauncher.ui.screens.targets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.expeknow.ariselauncher.ui.theme.*

@Composable
fun TargetsScreen(
    navController: NavController,
    viewModel: TargetsViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Targets",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Track your goals and achievements",
                style = MaterialTheme.typography.bodyMedium,
                color = BannerTextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (state.targets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No targets yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = BannerTextGray
                        )
                        Text(
                            text = "Add your first goal to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BannerTextGray.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.targets, key = { it.id }) { target ->
                        TargetCard(
                            target = target,
                            onEdit = { viewModel.onEvent(TargetsEvent.StartEditTarget(target)) },
                            onDelete = { viewModel.onEvent(TargetsEvent.DeleteTarget(target.id)) },
                            onProgressChange = { progress ->
                                viewModel.onEvent(TargetsEvent.UpdateProgress(target.id, progress))
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onEvent(TargetsEvent.ShowAddDialog) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = AccentGreen,
            contentColor = Color.Black
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Target",
                tint = Color.Black
            )
        }

        if (state.showAddDialog) {
            AddTargetDialog(
                editingTarget = state.editingTarget,
                onDismiss = { viewModel.onEvent(TargetsEvent.HideAddDialog) },
                onSave = { name, description, endDate, showOnHomeScreen ->
                    if (state.editingTarget != null) {
                        viewModel.onEvent(
                            TargetsEvent.UpdateTarget(
                                state.editingTarget!!.copy(
                                    name = name,
                                    description = description,
                                    endDate = endDate,
                                    showOnHomeScreen = showOnHomeScreen
                                )
                            )
                        )
                    } else {
                        viewModel.onEvent(TargetsEvent.AddTarget(name, description, endDate, showOnHomeScreen))
                    }
                }
            )
        }
    }
}

