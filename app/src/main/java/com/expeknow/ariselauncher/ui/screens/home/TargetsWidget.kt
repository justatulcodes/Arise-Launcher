package com.expeknow.ariselauncher.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expeknow.ariselauncher.data.datasource.Target
import com.expeknow.ariselauncher.ui.theme.AccentGreen
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.theme.SurfaceCard
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun CompactTargetsWidget(
    targets: List<Target>,
    modifier: Modifier = Modifier
) {
    if (targets.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active Targets",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = "${targets.size} goal${if (targets.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = BannerTextGray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(targets.take(5)) { target ->
                CompactTargetCard(target = target)
            }
        }
    }
}

@Composable
private fun CompactTargetCard(target: Target) {
    val daysLeft = calculateDaysLeft(target.endDate)
    val progressColor = getProgressColor(target.progress)
    val isOverdue = daysLeft < 0

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = target.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when {
                            isOverdue -> "Overdue"
                            daysLeft == 0L -> "Today"
                            daysLeft == 1L -> "1 day"
                            daysLeft <= 7 -> "$daysLeft days"
                            else -> formatShortDate(target.endDate)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            isOverdue -> Color.Red
                            daysLeft <= 3 -> Color(0xFFFF9800)
                            else -> BannerTextGray
                        },
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "${target.progress.toInt()}%",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(target.progress / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(progressColor)
                )
            }
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

private fun formatShortDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
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

