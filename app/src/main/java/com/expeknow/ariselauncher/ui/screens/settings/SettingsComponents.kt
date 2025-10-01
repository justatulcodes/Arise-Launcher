package com.expeknow.ariselauncher.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsCard(
    theme: SettingsTheme,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = theme.background,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    theme: SettingsTheme,
    badge: (@Composable () -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = theme.accent
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.White.copy(alpha = 0.2f),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }

        badge?.invoke()

        description?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    unit: String,
    description: String,
    theme: SettingsTheme
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                "${value.toInt()}$unit",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.accent
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = theme.accent,
                activeTrackColor = theme.accent,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            description,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun AppListItem(
    app: SettingsAppInfo,
    onEssentialToggle: (String) -> Unit,
    theme: SettingsTheme,
    showDivider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(theme.background, RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = theme.border,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = app.icon,
                        contentDescription = app.name,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        app.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        app.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (app.essential) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.border),
                        color = Color.Transparent,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Essential",
                                tint = theme.accent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "ESSENTIAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = theme.accent
                            )
                        }
                    }
                }

                Switch(
                    checked = app.essential,
                    onCheckedChange = { onEssentialToggle(app.id) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.2f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(
    title: String,
    theme: SettingsTheme,
    icon: ImageVector? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ),
            color = theme.accent
        )
    }
}

@Composable
fun DangerZoneCard(
    onShowResetPointsDialog: () -> Unit,
    onShowFactoryResetDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE57373).copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color(0xFFE57373).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Danger",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "DANGER ZONE",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE57373)
                )
            }

            Button(
                onClick = onShowResetPointsDialog,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE57373).copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "RESET ALL POINTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFE57373)
                )
            }

            Button(
                onClick = onShowFactoryResetDialog,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE57373).copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "FACTORY RESET",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFE57373)
                )
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "CONFIRM",
    cancelText: String = "CANCEL",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    theme: SettingsTheme,
    isDestructive: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isDestructive) Color(0xFFE57373) else theme.accent
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) Color(0xFFE57373).copy(alpha = 0.2f) else theme.accent.copy(
                        alpha = 0.2f
                    )
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isDestructive) Color(0xFFE57373) else theme.accent
                )
            ) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDestructive) Color(0xFFE57373) else theme.accent
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = cancelText,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = if (isDestructive) Color(0xFFE57373) else theme.accent,
        textContentColor = Color.White.copy(alpha = 0.8f)
    )
}

@Preview
@Composable
fun PreviewSettingsCard() {
    SettingsCard(theme = SettingsTheme()) {
        Text("Settings Card")
    }
}

@Preview
@Composable
fun PreviewSettingsToggleItem() {
    SettingsToggleItem(
        title = "Toggle Item",
        subtitle = "Subtitle",
        checked = true,
        onCheckedChange = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun PreviewSettingsSliderItem() {
    SettingsSliderItem(
        title = "Slider Item",
        value = 50f,
        onValueChange = {},
        valueRange = 0f..100f,
        steps = 10,
        unit = "%",
        description = "Description",
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun PreviewAppListItem() {
    AppListItem(
        app = SettingsAppInfo(
            id = "id",
            name = "App Name",
            icon = Icons.Default.Android,
            category = AppCategory.ESSENTIAL,
            essential = true
        ),
        onEssentialToggle = {},
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun PreviewSettingsSectionTitle() {
    SettingsSectionTitle(
        title = "Section Title",
        theme = SettingsTheme()
    )
}

@Preview
@Composable
fun PreviewDangerZoneCard() {
    DangerZoneCard(
        onShowResetPointsDialog = {},
        onShowFactoryResetDialog = {}
    )
}

@Preview
@Composable
fun PreviewConfirmationDialog() {
    ConfirmationDialog(
        title = "RESET ALL POINTS",
        message = "This action will permanently reset all points for all apps. This cannot be undone.",
        onConfirm = {},
        onDismiss = {},
        theme = SettingsTheme()
    )
}