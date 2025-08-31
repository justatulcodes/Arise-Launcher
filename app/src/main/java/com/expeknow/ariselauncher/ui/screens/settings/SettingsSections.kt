package com.expeknow.ariselauncher.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TaskCompletionSection(
    hideCompletedTasks: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "TASK COMPLETION",
            subtitle = if (hideCompletedTasks) {
                "Remove completed tasks from view"
            } else {
                "Show completed tasks as strikethrough"
            },
            description = if (hideCompletedTasks) {
                "Tasks will be removed and moved to task history"
            } else {
                "Tasks will remain visible with strikethrough styling"
            },
            checked = hideCompletedTasks,
            onCheckedChange = onToggle,
            theme = theme
        )
    }
}

@Composable
fun TunnelVisionSection(
    tunnelVisionMode: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "TUNNEL VISION MODE",
            subtitle = "Hide all apps except tasks on home screen",
            checked = tunnelVisionMode,
            onCheckedChange = onToggle,
            theme = theme,
            badge = if (tunnelVisionMode) {
                {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                        color = Color.Transparent
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "ACTIVE - MAXIMUM FOCUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = theme.accent
                            )
                        }
                    }
                }
            } else null
        )
    }
}

@Composable
fun AccessDelaysSection(
    appDrawerDelay: Float,
    distractionAppsDelay: Float,
    onAppDrawerDelayChange: (Float) -> Unit,
    onDistractionDelayChange: (Float) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "ACCESS DELAYS",
                style = MaterialTheme.typography.titleMedium,
                color = theme.accent
            )

            SettingsSliderItem(
                title = "App Drawer Delay",
                value = appDrawerDelay,
                onValueChange = onAppDrawerDelayChange,
                valueRange = 0f..300f,
                steps = 19, // 300/15 - 1
                unit = "s",
                description = "Delay before app drawer opens",
                theme = theme
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            SettingsSliderItem(
                title = "Distraction App Delay",
                value = distractionAppsDelay,
                onValueChange = onDistractionDelayChange,
                valueRange = 0f..120f,
                steps = 11, // 120/10 - 1
                unit = "s",
                description = "Additional delay for social/entertainment apps",
                theme = theme
            )
        }
    }
}

@Composable
fun PointSystemSection(
    pointThreshold: Float,
    warningsEnabled: Boolean,
    onThresholdChange: (Float) -> Unit,
    onWarningsToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "POINT SYSTEM",
                style = MaterialTheme.typography.titleMedium,
                color = theme.accent
            )

            SettingsSliderItem(
                title = "Block Threshold",
                value = pointThreshold,
                onValueChange = onThresholdChange,
                valueRange = 0f..200f,
                steps = 7, // 200/25 - 1
                unit = " pts",
                description = "Apps get blocked below this point level",
                theme = theme
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            SettingsToggleItem(
                title = "Distraction Warnings",
                subtitle = "Show warnings when adding distracting apps",
                checked = warningsEnabled,
                onCheckedChange = onWarningsToggle,
                theme = theme
            )
        }
    }
}

@Composable
fun EssentialAppsSection(
    apps: List<SettingsAppInfo>,
    onAppToggle: (String) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "ESSENTIAL APPS",
                style = MaterialTheme.typography.titleMedium,
                color = theme.accent,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            apps.forEachIndexed { index, app ->
                AppListItem(
                    app = app,
                    onEssentialToggle = onAppToggle,
                    theme = theme,
                    showDivider = index < apps.size - 1
                )
            }
        }
    }
}

@Composable
fun ResourcesSection(
    theme: SettingsTheme
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        SettingsSectionTitle(
            title = "RESOURCES & LINKS",
            theme = theme,
            icon = Icons.Filled.Link
        )

        Spacer(modifier = Modifier.height(12.dp))

        // This would typically be populated with actual resource data
        // For now, it's a placeholder
        Text(
            "Resource links will be displayed here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

// Add preview functions at the end of the file
@Preview
@Composable
fun TaskCompletionSectionPreview() {
    TaskCompletionSection(
        hideCompletedTasks = true,
        onToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun TunnelVisionSectionPreview() {
    TunnelVisionSection(
        tunnelVisionMode = true,
        onToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun AccessDelaysSectionPreview() {
    AccessDelaysSection(
        appDrawerDelay = 10f,
        distractionAppsDelay = 5f,
        onAppDrawerDelayChange = {},
        onDistractionDelayChange = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun PointSystemSectionPreview() {
    PointSystemSection(
        pointThreshold = 100f,
        warningsEnabled = true,
        onThresholdChange = {},
        onWarningsToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun EssentialAppsSectionPreview() {
    EssentialAppsSection(
        apps = listOf(
            SettingsAppInfo(
                id = "1",
                name = "App 1",
                essential = true,
                icon = Icons.Default.Android,
                category = AppCategory.ESSENTIAL
            ),
            SettingsAppInfo(
                id = "2",
                name = "App 2",
                essential = false,
                icon = Icons.Default.Apps,
                category = AppCategory.PRODUCTIVITY
            )
        ),
        onAppToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun ResourcesSectionPreview() {
    ResourcesSection(
        theme = SettingsTheme()
    )
}