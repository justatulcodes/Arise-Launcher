package com.expeknow.ariselauncher.ui.screens.apps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import com.expeknow.ariselauncher.ui.screens.home.Utils.toImageBitmap
import com.google.accompanist.drawablepainter.rememberDrawablePainter

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
                    border = BorderStroke(
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

//        IconButton(onClick = onClose) {
//            Icon(
//                imageVector = Icons.Default.Close,
//                contentDescription = "Close",
//                tint = Color.White.copy(alpha = 0.6f)
//            )
//        }
    }
}

@Composable
fun AppDrawerSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    theme: AppDrawerTheme
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(12.dp)
            )
            .background(theme.background, RoundedCornerShape(12.dp))
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = theme.accent.copy(alpha = 0.7f),
            modifier = Modifier.size(36.dp).padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Search apps...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(theme.accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                )
            )
        }

        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onSearchQueryChange("") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
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
                border = BorderStroke(
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
    Column{
        apps.chunked(columns).forEach { rowApps ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
    Column() {
        Box(
            modifier = Modifier
                .size(64.dp)
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
            app.icon?.toImageBitmap()?.let {
                Image(
                    contentDescription = app.name,
                    modifier = Modifier.size(54.dp),
                    bitmap = it
                )
            }

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

        //trim app name if too long and add ...
        val appName = if (app.name.length > 12) {
            app.name.substring(0,9) + "..."
        } else {
            app.name
        }
        Text(
            text = appName,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
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
fun SearchResultsSection(
    searchQuery: String,
    searchResults: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (searchResults.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SEARCH RESULTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.accent,
                    fontWeight = FontWeight.Medium
                )

                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = theme.accent.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${searchResults.size} found",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            AppGrid(
                apps = searchResults,
                onAppClick = onAppClick,
                theme = theme
            )
        } else {
            // No results found
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = "No results",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "NO APPS FOUND",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Try adjusting your search terms",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
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
                            fontStyle = FontStyle.Italic
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

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppDrawerSearchBarPreview() {
    AppDrawerSearchBar(
        searchQuery = "",
        onSearchQueryChange = {},
        theme = AppDrawerTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppDrawerSearchBarWithTextPreview() {
    AppDrawerSearchBar(
        searchQuery = "Instagram",
        onSearchQueryChange = {},
        theme = AppDrawerTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CountdownScreenPreview() {
    CountdownScreen(
        countdown = 30,
        theme = AppDrawerTheme(),
        onReturnToTasks = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppDrawerHeaderPreview() {
    AppDrawerHeader(
        theme = AppDrawerTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WarningBannerPreview() {
    WarningBanner()
}

//@Preview(showBackground = true, backgroundColor = 0xFF000000)
//@Composable
//private fun AppCategorySectionPreview() {
//    val sampleApps = listOf(
//        AppDrawerApp(
//            id = "1",
//            name = "Phone",
//            icon = Icons.Default.Phone,
//            category = AppCategory.ESSENTIAL,
//            pointCost = 0,
//            description = "Make calls",
//            packageName = "com.android.phone"
//        ),
//        AppDrawerApp(
//            id = "2",
//            name = "Instagram",
//            icon = Icons.Default.CameraAlt,
//            category = AppCategory.SOCIAL,
//            pointCost = 25,
//            description = "Social media",
//            packageName = "com.android.phone"
//
//        )
//    )
//
//    AppCategorySection(
//        category = AppCategory.ESSENTIAL,
//        apps = sampleApps,
//        onAppClick = {},
//        theme = AppDrawerTheme()
//    )
//}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppDrawerFooterPreview() {
    AppDrawerFooter(
        currentPoints = 245,
        totalFreeApps = 8,
        totalPremiumApps = 12,
        theme = AppDrawerTheme()
    )
}

//@Preview(showBackground = true, backgroundColor = 0xFF000000)
//@Composable
//private fun AppWarningDialogPreview() {
//    val sampleApp = AppDrawerApp(
//        id = "1",
//        name = "Instagram",
//        icon = Icons.Default.CameraAlt,
//        category = AppCategory.SOCIAL,
//        pointCost = 25,
//        description = "Social media platform",
//        packageName = "com.android.phone"
//
//    )
//
//    AppWarningDialog(
//        app = sampleApp,
//        onConfirm = {},
//        onDismiss = {}
//    )
//}