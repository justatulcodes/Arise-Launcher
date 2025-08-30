package com.expeknow.ariselauncher.ui.screens.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CountdownScreen(
    countdown: Int,
    theme: AppDrawerTheme,
    onReturnToTasks: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Clock",
                    tint = theme.accent,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACCESS DELAYED",
                    style = MaterialTheme.typography.headlineSmall,
                    color = theme.accent
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Full app drawer unlocks in...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(
                            width = 4.dp,
                            color = theme.border,
                            shape = CircleShape
                        )
                        .background(
                            theme.background,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = countdown.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        color = theme.accent
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = (60 - countdown) / 60f,
                    modifier = Modifier
                        .height(4.dp)
                        .width(200.dp),
                    color = theme.accent,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "This delay prevents impulsive app usage.\nUse this time to reconsider your priorities.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onReturnToTasks,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color.White
                    )
                ) {
                    Text("RETURN TO TASKS")
                }
            }
        }
    }
}

@Composable
fun AppDrawerHeader(
    onClose: () -> Unit,
    theme: AppDrawerTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.background)
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ALL APPS",
                style = MaterialTheme.typography.titleLarge,
                color = theme.accent
            )
            Text(
                text = "Complete app collection",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun WarningBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFD54F).copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "POINT BURN ZONE",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFFD54F),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Distraction apps consume points. Choose wisely.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AppCategorySection(
    category: AppCategory,
    apps: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.displayName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = category.color,
                fontWeight = FontWeight.Medium
            )

            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = category.color.copy(alpha = 0.3f)
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${apps.size} apps",
                    style = MaterialTheme.typography.labelSmall,
                    color = category.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        AppGrid(
            apps = apps,
            onAppClick = onAppClick,
            theme = theme
        )
    }
}

@Composable
fun AppGrid(
    apps: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme
) {
    val columns = 4
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        apps.chunked(columns).forEach { rowApps ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowApps.forEach { app ->
                    Box(modifier = Modifier.weight(1f)) {
                        AppGridItem(
                            app = app,
                            onAppClick = onAppClick,
                            theme = theme
                        )
                    }
                }
                if (rowApps.size < columns) {
                    repeat(columns - rowApps.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppGridItem(
    app: AppDrawerApp,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = theme.border,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onAppClick(app) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = app.icon,
                contentDescription = app.name,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            if (app.pointCost > 0) {
                Surface(
                    color = when {
                        app.pointCost <= 5 -> Color(0xFFFFD54F).copy(alpha = 0.2f)
                        app.pointCost <= 15 -> Color(0xFFFFB74D).copy(alpha = 0.2f)
                        else -> Color(0xFFE57373).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = when {
                            app.pointCost <= 5 -> Color(0xFFFFD54F).copy(alpha = 0.4f)
                            app.pointCost <= 15 -> Color(0xFFFFB74D).copy(alpha = 0.4f)
                            else -> Color(0xFFE57373).copy(alpha = 0.4f)
                        }
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                ) {
                    Text(
                        text = "-${app.pointCost}",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            app.pointCost <= 5 -> Color(0xFFFFD54F)
                            app.pointCost <= 15 -> Color(0xFFFFB74D)
                            else -> Color(0xFFE57373)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = app.name,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = app.description,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun AppDrawerFooter(
    currentPoints: Int,
    totalFreeApps: Int,
    totalPremiumApps: Int,
    theme: AppDrawerTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.background)
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CURRENT POINTS: $currentPoints",
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.accent
                )

                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Text(
                    text = "FREE APPS: $totalFreeApps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF81C784)
                )

                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Text(
                    text = "PREMIUM APPS: $totalPremiumApps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE57373)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Complete tasks to earn more points for app access",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun AppWarningDialog(
    app: AppDrawerApp?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (app != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.Black,
            shape = RoundedCornerShape(12.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFE57373)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "DISTRACTION WARNING",
                        color = Color(0xFFE57373)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Opening ${app.name} will cost ${app.pointCost} points and may disrupt your focus.",
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "\"True discipline means resisting instant gratification.\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373).copy(alpha = 0.2f),
                        contentColor = Color(0xFFE57373)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE57373).copy(alpha = 0.4f)
                    )
                ) {
                    Text("USE ANYWAY (-${app.pointCost} pts)")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                ) {
                    Text("STAY FOCUSED")
                }
            }
        )
    }
}