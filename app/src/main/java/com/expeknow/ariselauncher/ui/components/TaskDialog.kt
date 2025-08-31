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

@Composable
fun TaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (title: String, description: String, points: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var pointsValue by remember { mutableStateOf(10f) }
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

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = pointsValue,
                    onValueChange = { pointsValue = it },
                    valueRange = 0f..50f,
                    steps = 49,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = AccentGreen,
                        activeTrackColor = AccentGreen,
                        inactiveTrackColor = DividerGray
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "0",
                        color = BannerTextGray,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "50",
                        color = BannerTextGray,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onTaskAdded(title, description, points)
                        onDismiss()
                    }
                },
                modifier = Modifier.background(Color.White)
            ) {
                Text("Add Task", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.background(SurfaceCard)) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = SurfaceCard
    )
}

@Preview
@Composable
fun AddTaskDialogPreview() {
    TaskDialog(onDismiss = {}, onTaskAdded = { _, _, _ -> })
}