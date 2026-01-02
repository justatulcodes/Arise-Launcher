package com.expeknow.ariselauncher.ui.screens.targets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.expeknow.ariselauncher.data.datasource.Target
import com.expeknow.ariselauncher.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun TargetCard(
    target: Target,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val daysLeft = calculateDaysLeft(target.endDate)

    // Auto-calculate progress based on time elapsed
    val autoProgress = if (target.isCompleted) {
        100f
    } else {
        calculateAutoProgress(target.createdAt, target.endDate)
    }

    val progressColor = if (target.isCompleted) AccentGreen else getProgressColor(autoProgress)
    val daysLeftColor = getDaysLeftColor(daysLeft, target.isCompleted)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (target.isCompleted) SurfaceCard.copy(alpha = 0.6f) else SurfaceCard
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left side - Title and description
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (target.isCompleted) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = AccentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = target.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = if (target.isCompleted) Color.White.copy(alpha = 0.7f) else Color.White
                        )
                    }

                    if (target.description.isNotEmpty()) {
                        Text(
                            text = target.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BannerTextGray.copy(alpha = if (target.isCompleted) 0.6f else 1f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "End: ${formatDate(target.endDate)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = BannerTextGray.copy(alpha = if (target.isCompleted) 0.6f else 1f)
                    )
                }

                // Right side - Days left (prominent)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (target.isCompleted) "Mark Incomplete" else "Mark Complete") },
                                onClick = {
                                    showMenu = false
                                    onToggleComplete()
                                },
                                leadingIcon = {
                                    Icon(
                                        if (target.isCompleted) Icons.Default.Refresh else Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showMenu = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Prominent days left display
                    if (target.isCompleted) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = AccentGreen
                        )
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            text = when {
                                daysLeft < 0 -> "${-daysLeft}"
                                daysLeft == 0L -> "0"
                                else -> "$daysLeft"
                            },
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = daysLeftColor
                        )
                        Text(
                            text = when {
                                daysLeft < 0 -> "days overdue"
                                daysLeft == 0L -> "Due today"
                                daysLeft == 1L -> "day left"
                                else -> "days left"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = daysLeftColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar with percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(autoProgress / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(progressColor)
                    )
                }

                Text(
                    text = "${autoProgress.toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = progressColor,
                    modifier = Modifier.width(40.dp)
                )
            }

            if (!target.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))

                // Mark as complete button
                OutlinedButton(
                    onClick = onToggleComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AccentGreen
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AccentGreen.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark as Complete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTargetDialog(
    editingTarget: Target?,
    onDismiss: () -> Unit,
    onSave: (String, String, Long) -> Unit
) {
    var name by remember { mutableStateOf(editingTarget?.name ?: "") }
    var description by remember { mutableStateOf(editingTarget?.description ?: "") }
    var selectedDate by remember {
        mutableStateOf(editingTarget?.endDate ?: System.currentTimeMillis())
    }
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (editingTarget != null) "Edit Target" else "Add New Target",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = AccentGreen,
                        unfocusedLabelColor = BannerTextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedLabelColor = AccentGreen,
                        unfocusedLabelColor = BannerTextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { showDatePicker = true }
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "End Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = BannerTextGray
                        )
                        Text(
                            text = formatDate(selectedDate),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name, description, selectedDate)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen
                        )
                    ) {
                        Text("Save", color = Color.Black)
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun calculateDaysLeft(endDate: Long): Long {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val diff = endDate - today
    return TimeUnit.MILLISECONDS.toDays(diff)
}

private fun calculateTotalDays(createdAt: Long, endDate: Long): Long {
    val diff = endDate - createdAt
    return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1)
}

private fun calculateAutoProgress(createdAt: Long, endDate: Long): Float {
    val now = System.currentTimeMillis()
    val totalDuration = (endDate - createdAt).toFloat()
    val elapsed = (now - createdAt).toFloat()

    if (totalDuration <= 0) return 100f

    return ((elapsed / totalDuration) * 100f).coerceIn(0f, 100f)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun getProgressColor(progress: Float): Color {
    return when {
        progress >= 75f -> AccentGreen
        progress >= 50f -> Color(0xFF4CAF50)
        progress >= 25f -> Color(0xFFFF9800)
        else -> Color(0xFFFF5722)
    }
}

private fun getDaysLeftColor(daysLeft: Long, isCompleted: Boolean): Color {
    if (isCompleted) return AccentGreen

    return when {
        daysLeft < 0 -> Color(0xFFFF5252) // Red for overdue
        daysLeft == 0L -> Color(0xFFFF9800) // Orange for due today
        daysLeft <= 3 -> Color(0xFFFFB74D) // Light orange for urgent (1-3 days)
        daysLeft <= 7 -> Color(0xFFFFD54F) // Yellow for soon (4-7 days)
        daysLeft <= 14 -> Color(0xFF81C784) // Light green for moderate (8-14 days)
        else -> AccentGreen // Green for plenty of time
    }
}

