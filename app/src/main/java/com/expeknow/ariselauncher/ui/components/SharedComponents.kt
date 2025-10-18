package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.ui.screens.home.Utils.getDayAbbreviation

@Composable
fun DayChip(
    day: DaysOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = Color(0xFF4ADE80), // Default green
    unselectedColor: Color = Color.White.copy(alpha = 0.1f),
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                if (isSelected) selectedColor else unselectedColor,
                CircleShape
            )
            .border(
                1.dp,
                if (isSelected) selectedColor else Color.White.copy(alpha = 0.2f),
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getDayAbbreviation(day),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}