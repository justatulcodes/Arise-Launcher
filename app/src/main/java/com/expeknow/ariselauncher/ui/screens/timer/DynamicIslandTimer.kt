package com.expeknow.ariselauncher.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.runtime.mutableFloatStateOf
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun DynamicIslandTimer(
    isVisible: Boolean,
    appName: String,
    elapsedSeconds: Int,
    pointsLost: Int,
    modifier: Modifier = Modifier,
    onPositionChange: ((x: Int, y: Int) -> Unit)? = null
) {

    TimerCapsule(
        elapsedSeconds = elapsedSeconds,
        pointsLost = pointsLost,
        onPositionChange = onPositionChange,
        modifier = modifier
    )
}



@Composable
private fun TimerCapsule(
    elapsedSeconds: Int,
    pointsLost: Int,
    modifier: Modifier = Modifier,
    onPositionChange: ((x: Int, y: Int) -> Unit)? = null
) {

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1F1F1F))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    onPositionChange?.invoke(offsetX.roundToInt(), offsetY.roundToInt())
                }
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerItem(
                label = "Time",
                value = formatTime(elapsedSeconds),
                color = Color.White
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(16.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            TimerItem(
                label = "Points",
                value = "-$pointsLost",
                color = Color(0xFFE57373)
            )
        }
    }
}




@Composable
private fun TimerItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            color = Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            lineHeight = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (minutes > 0) {
        String.format(Locale.US, "%dm %02ds", minutes, remainingSeconds)
    } else {
        String.format(Locale.US, "%ds", remainingSeconds)
    }
}
