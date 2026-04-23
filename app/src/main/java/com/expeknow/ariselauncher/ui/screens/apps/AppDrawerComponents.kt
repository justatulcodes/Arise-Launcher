package com.expeknow.ariselauncher.ui.screens.apps

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.expeknow.ariselauncher.ui.screens.home.Utils.toImageBitmap
import com.expeknow.ariselauncher.ui.theme.SurfaceCard
import kotlin.math.roundToInt

private object AppContextMenuDefaults {
    val MENU_WIDTH = 220.dp
    val ICON_SIZE = 64.dp
    val MENU_OFFSET_X = 26.dp
    val MENU_OFFSET_Y = (-36).dp
    val DESTRUCTIVE_ACTION_COLOR = Color(0xFFE57373)
}

@Composable
fun AppContextMenu(
    app: AppDrawerApp,
    offset: IntOffset,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    onAppInfo: () -> Unit,
    onUpdateAppStartTimer :(Long) -> Unit = {},
    theme: AppDrawerTheme
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )
    
    Popup(
        alignment = Alignment.TopStart,
        offset = offset,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
            shape = RoundedCornerShape(12.dp),
            color = SurfaceCard,
            border = BorderStroke(1.dp, theme.border),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .width(AppContextMenuDefaults.MENU_WIDTH)
                    .padding(vertical = 4.dp)
            ) {
                // App Info option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppInfo()
                            onDismiss()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "App Info",
                        tint = theme.accent.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "App Info",
                        style = MaterialTheme.typography.bodyMedium,
                        color = theme.accent.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Normal,
                    )
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = theme.border
                )

                Text(
                    text = "Start Timer",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 6.dp)
                )

                val timerOptions = listOf(10L, 30L, 60L, 90L)
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timerOptions.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowOptions.forEach { option ->
                                val isSelected = app.appStartTimerValue == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        onUpdateAppStartTimer(if (isSelected) 0L else option)
                                    },
                                    label = { Text("${option}s") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowOptions.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountdownScreen(
    countdown: Int,
    theme: AppDrawerTheme,
    onReturnToTasks: () -> Unit,
    appDrawerDelay: Float
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
                    progress = (appDrawerDelay - countdown) / appDrawerDelay,
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

    }
}

@Composable
fun AppDrawerSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    theme: AppDrawerTheme,
    focusRequester: FocusRequester? = null
) {
    Row(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
//            .border(
//                width = 1.dp,
//                color = theme.border,
//                shape = RoundedCornerShape(16.dp)
//            )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (focusRequester != null) {
                            Modifier.focusRequester(focusRequester)
                        } else {
                            Modifier
                        }
                    ),
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
                    modifier = Modifier.size(24.dp).padding(end = 8.dp)
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
    theme: AppDrawerTheme,
    onUpdateAppStartTimer: (AppDrawerApp, Long) -> Unit = { _, _ -> }
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
            theme = theme,
            onUpdateAppStartTimer = onUpdateAppStartTimer
        )
    }
}

@Composable
fun AppGrid(
    apps: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme,
    onUpdateAppStartTimer: (AppDrawerApp, Long) -> Unit = { _, _ -> }
) {
    val columns = 4
    Column{
        apps.chunked(columns).forEach { rowApps ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowApps.forEach { app ->
                    Box(modifier = Modifier.weight(1f)) {
                        AppGridItemV2(
                            app = app,
                            onAppClick = onAppClick,
                            theme = theme,
                            onUpdateAppStartTimer = onUpdateAppStartTimer
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
fun AppGridItemV2(
    app: AppDrawerApp,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme = AppDrawerTheme(),
    onUpdateAppStartTimer: (AppDrawerApp, Long) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var showContextMenu by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    
    // Calculate menu offset based on icon size
    val menuOffset = with(density) {
        IntOffset(
            AppContextMenuDefaults.MENU_OFFSET_X.roundToPx(),
            AppContextMenuDefaults.MENU_OFFSET_Y.roundToPx()
        )
    }
    
    val scale by animateFloatAsState(
        targetValue = if (showContextMenu) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )
    
    Box {
        Column() {
            Box(
                modifier = Modifier
                    .size(AppContextMenuDefaults.ICON_SIZE)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .combinedClickable(
                        onClick = { onAppClick(app) },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showContextMenu = true
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                app.icon?.toImageBitmap()?.let {
                    Image(
                        contentDescription = app.name,
                        modifier = Modifier.size(56.dp),
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
                        border = BorderStroke(
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
        
        if (showContextMenu) {
            AppContextMenu(
                app = app,
                offset = menuOffset,
                onDismiss = { showContextMenu = false },
                onUninstall = {
                    val intent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.parse("package:${app.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                onAppInfo = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${app.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                },
                onUpdateAppStartTimer = { launchTimerValue ->
                    onUpdateAppStartTimer(app, launchTimerValue)
                },
                theme = theme
            )
        }
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
    theme: AppDrawerTheme,
    onUpdateAppStartTimer: (AppDrawerApp, Long) -> Unit = { _, _ -> }
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
                theme = theme,
                onUpdateAppStartTimer = onUpdateAppStartTimer
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
fun AppTimerDialog(
    app: AppDrawerApp?,
    timerCountdown: Int,
    onDismiss: () -> Unit,
    theme: AppDrawerTheme
) {
    if (app != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, theme.border)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Timer",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "INSUFFICIENT POINTS",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // App Icon
                    app.icon?.toImageBitmap()?.let {
                        Image(
                            contentDescription = app.name,
                            modifier = Modifier.size(64.dp),
                            bitmap = it
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFE57373).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFE57373).copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = "COST: ${app.pointCost}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE57373),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = timerCountdown.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = Color(0xFFFFB74D),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "seconds",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LinearProgressIndicator(
                        progress = 1f - (timerCountdown.toFloat() / app.pointCost),
                        modifier = Modifier
                            .height(6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFFFFB74D),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Please wait to use this app",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Earn points by completing tasks for instant access",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cancel Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "CANCEL",
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopUsedAppsRow(
    apps: List<AppDrawerApp>,
    onAppClick: (AppDrawerApp) -> Unit,
    theme: AppDrawerTheme,
    onUpdateAppStartTimer: (AppDrawerApp, Long) -> Unit = { _, _ -> }
) {
    if (apps.isEmpty()) return
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            apps.take(4).forEach { app ->
                Box(modifier = Modifier.weight(1f)) {
                    AppGridItemV2(
                        app = app,
                        onAppClick = onAppClick,
                        theme = theme,
                        onUpdateAppStartTimer = onUpdateAppStartTimer
                    )
                }
            }
            // Fill remaining space if less than 4 apps
            if (apps.size < 4) {
                repeat(4 - apps.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
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
        onReturnToTasks = {},
        appDrawerDelay = 10f
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
