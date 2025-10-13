package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import com.expeknow.ariselauncher.ui.theme.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import com.expeknow.ariselauncher.data.model.TaskCategory

@Composable
fun TaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (title: String, description: String, points: Int, category: TaskCategory) -> Unit,
    showCategorySelector: Boolean = false,
    initialCategory: TaskCategory = TaskCategory.PERSONAL,
    availableCategories: List<TaskCategory> = TaskCategory.values().toList()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pointsValue by remember { mutableStateOf(10f) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val points = pointsValue.roundToInt()
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(windowInfo) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            if (isWindowFocused) {
                delay(200)
                focusRequester.requestFocus()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ADD NEW TASK", color = BannerTextGray) },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Task Name", color = BannerTextGray)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Enter task name...", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                )

                Text("Description", color = BannerTextGray)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter task description...", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    maxLines = 3,
                )

                if (showCategorySelector) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Category", color = BannerTextGray)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryDropdown = true }
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getCategoryName(selectedCategory),
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Select Category",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false },
                            modifier = Modifier
                                .background(Color(0xFF1A1A1A))
                                .width(300.dp)
                        ) {
                            availableCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(getCategoryName(category), color = Color.White) },
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Points: ", color = BannerTextGray)
                    Text(
                        text = "$points",
                        color = AccentGreen,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                }

                Slider(
                    value = pointsValue,
                    onValueChange = { pointsValue = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentGreen,
                        activeTrackColor = AccentGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotEmpty()) {
                        onTaskAdded(title, description, points, selectedCategory)
                    }
                },
                enabled = title.isNotEmpty()
            ) {
                Text("ADD", color = if (title.isNotEmpty()) AccentGreen else Color.Gray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        },
        containerColor = Color.Black,
        textContentColor = Color.White,
    )
}

private fun getCategoryName(category: TaskCategory): String {
    return when (category) {
        TaskCategory.PERSONAL -> "Personal"
        TaskCategory.WORK -> "Work"
        TaskCategory.URGENT -> "Urgent"
        TaskCategory.IMPORTANT -> "Important"
        TaskCategory.INTELLIGENCE -> "Intelligence"
        TaskCategory.PHYSICAL -> "Physical"
        TaskCategory.WEALTH -> "Wealth"
        TaskCategory.BECOMING_INTELLIGENT -> "Becoming Intelligent"
        TaskCategory.BECOMING_MUSCULAR -> "Becoming Muscular"
        TaskCategory.BECOMING_RICH -> "Becoming Rich"
        TaskCategory.MISCELLANEOUS -> "Miscellaneous"
    }
}

@Preview
@Composable
fun AddTaskDialogPreview() {
    TaskDialog(onDismiss = {}, onTaskAdded = { _, _, _, _ -> })
}