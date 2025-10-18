package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalWindowInfo
import com.expeknow.ariselauncher.ui.theme.*
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.Task

@Composable
fun TaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (title: String, description: String, points: Int, category: TaskCategory, isRepeated: Boolean, repeatDays: List<DaysOfWeek>) -> Unit,
    showCategorySelector: Boolean = false,
    initialCategory: TaskCategory = TaskCategory.PERSONAL,
    availableCategories: List<TaskCategory> = TaskCategory.values().toList()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pointsValue by remember { mutableStateOf(10f) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var isRepeated by remember { mutableStateOf(false) }
    var repeatDays by remember { mutableStateOf<List<DaysOfWeek>>(emptyList()) }

    val points = pointsValue.roundToInt()
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }

    val inputTextStyle = TextStyle(
        fontSize = 14.sp,
        color = Color.White
    )

    val labelTextStyle = TextStyle(
        fontSize = 12.sp,
        color = BannerTextGray,
        fontWeight = FontWeight.Medium
    )

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
        title = {
            Text(
                "ADD NEW TASK",
                color = BannerTextGray,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.padding(4.dp)) {
                Text("Task Name", style = labelTextStyle)
                Spacer(Modifier.height(2.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            "Enter task name...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .focusRequester(focusRequester),
                    singleLine = true,
                    textStyle = inputTextStyle,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Description", style = labelTextStyle)
                Spacer(Modifier.height(2.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter task description...",
                        color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    textStyle = inputTextStyle,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                if (showCategorySelector) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Category", style = labelTextStyle)
                    Spacer(Modifier.height(2.dp))

                    CategorySelectionChips(
                        availableCategories = availableCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = category
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Points: $points",
                        color = AccentGreen,
                        style = MaterialTheme.typography.bodyMedium
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

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Repeatable task",
                        style = labelTextStyle,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isRepeated,
                        onCheckedChange = { isRepeated = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentGreen,
                            checkedTrackColor = AccentGreen.copy(alpha = 0.2f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray.copy(alpha = 0.5f)
                        )
                    )
                }

                if(isRepeated) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Repeat on days:", style = labelTextStyle)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DaySelectionChips(
                                selectedDays = repeatDays,
                                onDaySelected = { day, selected ->
                                    repeatDays = if (selected) {
                                        repeatDays + day
                                    } else {
                                        repeatDays - day
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotEmpty()) {
                        onTaskAdded(
                            title,
                            description,
                            points,
                            selectedCategory,
                            isRepeated,
                            if (isRepeated) repeatDays else emptyList()
                        )
                    }
                },
                enabled = title.isNotEmpty()
            ) {
                Text("ADD",
                    color = if (title.isNotEmpty()) AccentGreen else Color.Gray,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL",
                    color = Color.Gray,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
            }
        },
        containerColor = Color.Black,
        textContentColor = Color.White,
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun DaySelectionChips(
    selectedDays: List<DaysOfWeek>,
    onDaySelected: (DaysOfWeek, Boolean) -> Unit
) {
    val days = listOf(
        DaysOfWeek.MONDAY to "M",
        DaysOfWeek.TUESDAY to "T",
        DaysOfWeek.WEDNESDAY to "W",
        DaysOfWeek.THURSDAY to "T",
        DaysOfWeek.FRIDAY to "F",
        DaysOfWeek.SATURDAY to "S",
        DaysOfWeek.SUNDAY to "S"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { (day, label) ->
            val isSelected = selectedDays.contains(day)
            DayChip(
                day = day,
                label = label,
                isSelected = isSelected,
                onSelected = onDaySelected
            )
        }
    }
}

@Composable
private fun DayChip(
    day: DaysOfWeek,
    label: String,
    isSelected: Boolean,
    onSelected: (DaysOfWeek, Boolean) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(30.dp)
            .background(
                color = if (isSelected) AccentGreen else Color.Transparent,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                brush = SolidColor(if (isSelected) AccentGreen else Color.White.copy(alpha = 0.3f)),
                shape = CircleShape
            )
            .clickable { onSelected(day, !isSelected) }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun CategorySelectionChips(
    availableCategories: List<TaskCategory>,
    selectedCategory: TaskCategory,
    onCategorySelected: (TaskCategory) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        availableCategories.forEach { category ->
            val isSelected = category == selectedCategory
            CategoryChip(
                category = category,
                isSelected = isSelected,
                onSelected = onCategorySelected
            )
        }
    }

}

@Composable
private fun CategoryChip(
    category: TaskCategory,
    isSelected: Boolean,
    onSelected: (TaskCategory) -> Unit
) {
    val categoryName = getCategoryName(category)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable { onSelected(category) }
            .border(
                width = 1.dp,
                brush = SolidColor(if (isSelected) AccentGreen else Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isSelected) AccentGreen else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = categoryName,
            color = if (isSelected) Color.Black else Color.White,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
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
    TaskDialog(onDismiss = {}, onTaskAdded = { _, _, _, _, _, _ -> })
}