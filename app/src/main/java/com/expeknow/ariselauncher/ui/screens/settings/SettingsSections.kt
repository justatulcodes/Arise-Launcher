package com.expeknow.ariselauncher.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Home
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
fun ShowHomeScreenSection(
    showHomeScreen: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "SHOW HOME SCREEN",
            subtitle = "Toggle visibility of the blank home screen",
            description = "When enabled, shows the clock screen with essential apps as the first page",
            checked = showHomeScreen,
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
fun KeyboardTriggerSection(
    keyboardTriggerEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "AUTO-SHOW KEYBOARD",
            subtitle = "Automatically open keyboard in app drawer",
            description = "When enabled, the keyboard will appear immediately when you open the app drawer for quick app search",
            checked = keyboardTriggerEnabled,
            onCheckedChange = onToggle,
            theme = theme
        )
    }
}

@Composable
fun ShowCategorizedApps(
    categorizedAppsEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "SHOW CATEGORIZED APPS",
            subtitle = "Display apps grouped by categories in app drawer",
            description = "Helps in quickly locating apps based on their type or usage",
            checked = categorizedAppsEnabled,
            onCheckedChange = onToggle,
            theme = theme
        )
    }
}

@Composable
fun ShowWeeklyScheduleSection(
    isShowWeeklyScheduleEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "SHOW WEEKLY SCHEDULE",
            subtitle = "Display full week schedule in calendar view",
            description = "To be used in focused mode when you need to see your full week schedule at a glance",
            checked = isShowWeeklyScheduleEnabled,
            onCheckedChange = onToggle,
            theme = theme
        )
    }
}

@Composable
fun AppTimerSection(
    appTimerEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        SettingsToggleItem(
            title = "APP USAGE TIMER",
            subtitle = "Track time spent in other apps",
            description = "When enabled, the app will track how long you spend in other applications when you leave the launcher",
            checked = appTimerEnabled,
            onCheckedChange = onToggle,
            theme = theme
        )
    }
}

@Composable
fun AppLaunchPopupSection(
    appLaunchPopupEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "APP LAUNCH POPUP",
                style = MaterialTheme.typography.titleMedium,
                color = theme.accent
            )

            SettingsToggleItem(
                title = "App Launch Popup Delay",
                subtitle = "Show popup delay before launching apps in app drawer",
                checked = appLaunchPopupEnabled,
                onCheckedChange = onToggle,
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

        Text(
            "Resource links will be displayed here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun DefaultLauncherSection(
    isDefaultLauncher: Boolean,
    onSetDefaultLauncher: (Boolean) -> Unit,
    theme: SettingsTheme
) {
    SettingsCard(theme) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsToggleItem(
                title = "DEFAULT LAUNCHER",
                subtitle = "Set Arise Launcher as your home screen",
                description = "Make this app the default launcher that appears when you press the home button",
                checked = isDefaultLauncher,
                onCheckedChange = onSetDefaultLauncher,
                theme = theme,
                badge = if (isDefaultLauncher) {
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
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = theme.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "CURRENTLY ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = theme.accent
                                )
                            }
                        }
                    }
                } else null
            )

            if (!isDefaultLauncher) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Toggle will open system settings where you can select Arise as your default launcher",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
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
fun KeyboardTriggerSectionPreview() {
    KeyboardTriggerSection(
        keyboardTriggerEnabled = true,
        onToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun AppLaunchPopupSectionPreview() {
    AppLaunchPopupSection(
        appLaunchPopupEnabled = true,
        onToggle = {},
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

@Preview
@Composable
fun DefaultLauncherSectionPreview() {
    DefaultLauncherSection(
        isDefaultLauncher = true,
        onSetDefaultLauncher = {},
        theme = SettingsTheme()
    )
}
