package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.PointsHistory
import kotlin.math.max
import kotlin.math.min

@Composable
fun CustomChart(
    data: List<PointsHistory>,
    color: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    if (data.isEmpty()) return

    val maxPoints = data.maxOf { it.points }
    val minPoints = data.minOf { it.points }
    val range = maxPoints - minPoints
    val padding = range * 0.1f
    val adjustedMin = minPoints - padding
    val adjustedMax = maxPoints + padding
    val adjustedRange = adjustedMax - adjustedMin

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val chartWidth = canvasWidth - 60.dp.toPx()
        val chartHeight = canvasHeight - 40.dp.toPx()
        val startX = 30.dp.toPx()
        val startY = 20.dp.toPx()

        // Draw grid lines
        val gridColor = Color.White.copy(alpha = 0.1f)
        val gridPath = Path()
        for (i in 0..5) {
            val y = startY + (i * chartHeight / 5)
            gridPath.moveTo(startX, y)
            gridPath.lineTo(startX + chartWidth, y)
        }
        for (i in 0..6) {
            val x = startX + (i * chartWidth / 6)
            gridPath.moveTo(x, startY)
            gridPath.lineTo(x, startY + chartHeight)
        }

        drawPath(
            path = gridPath,
            color = gridColor,
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
            )
        )

        // Calculate points for the line
        val points = data.mapIndexed { index, item ->
            val x = startX + (index.toFloat() / (data.size - 1)) * chartWidth
            val y =
                startY + chartHeight - ((item.points - adjustedMin) / adjustedRange) * chartHeight
            Offset(x, y)
        }

        // Draw the line
        if (points.size > 1) {
            val path = Path()
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // Draw data points
        points.forEach { point ->
            drawCircle(
                color = Color.Black,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = point
            )
        }

        // Draw X-axis labels
        data.forEachIndexed { index, item ->
            val x = startX + (index.toFloat() / (data.size - 1)) * chartWidth
            val y = startY + chartHeight + 15.dp.toPx()

            val textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp
            )

            val textLayoutResult = textMeasurer.measure(item.day, textStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x - textLayoutResult.size.width / 2,
                    y
                )
            )
        }
    }
}