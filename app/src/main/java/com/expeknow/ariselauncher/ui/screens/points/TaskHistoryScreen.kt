package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expeknow.ariselauncher.data.model.Rank
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType
import com.expeknow.ariselauncher.data.model.ranks
import com.expeknow.ariselauncher.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHistoryScreen(
    navController: NavController,
    completedTasks: List<Task>,
    currentRank: Rank = ranks[0]
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "COMPLETED TASKS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    ),
                    color = currentRank.colors.accent
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = currentRank.colors.accent
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color.Black
            ),
            windowInsets = WindowInsets(top = 0.dp)
        )

        if (completedTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No completed tasks yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            items(completedTasks) { task ->
                CompletedTaskItem(task = task, currentRank = currentRank, navController = navController)
            }
        }
    }
}

@Composable
fun CompletedTaskItem(
    task: Task,
    currentRank: Rank,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            navController.navigate(Screen.TaskDetails.routeFor(task.id))
        },
        colors = CardDefaults.cardColors(containerColor = currentRank.colors.background),
        border = BorderStroke(1.dp, currentRank.colors.border),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = currentRank.colors.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                task.completedAt?.let { timestamp ->
                    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category chip
                Surface(
                    color = currentRank.colors.background,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, currentRank.colors.border)
                ) {
                    Text(
                        text = task.category.name.replace('_', ' '),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Points indicator
                Text(
                    text = "+${task.points} PTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = currentRank.colors.accent
                )
            }

            // Links (if any)
            if (task.relatedLinks.isNotEmpty()) {
                Divider(color = currentRank.colors.border.copy(alpha = 0.5f), thickness = 1.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${task.relatedLinks.size} ${if (task.relatedLinks.size == 1) "link" else "links"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

//            // View details button
//            TextButton(
//                onClick = { navController.navigate(Screen.TaskDetails.routeFor(task.id)) },
//                colors = ButtonDefaults.textButtonColors(
//                    contentColor = currentRank.colors.accent
//                ),
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Text("View Details")
//            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun TaskHistoryScreenPreview() {
    val sampleTasks = listOf(
        Task(
            id = "1",
            title = "Complete Project Presentation",
            description = "Finish the quarterly presentation for the team meeting",
            points = 50,
            category = TaskCategory.WORK,
            isCompleted = true,
            completedAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
            relatedLinks = listOf(
                TaskLink(
                    url = "https://example.com",
                    title = "Presentation Template",
                    type = TaskLinkType.LINK
                )
            ),
            isRepeated = false
        ),
        Task(
            id = "2",
            title = "Daily Workout Routine",
            description = "Complete 30 minutes of cardio and strength training",
            points = 20,
            category = TaskCategory.PHYSICAL,
            isCompleted = true,
            completedAt = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
            isRepeated = false
        ),
        Task(
            id = "3",
            title = "Read Book Chapter",
            description = "Read chapter 5 of 'Deep Work' by Cal Newport",
            points = 30,
            category = TaskCategory.INTELLIGENCE,
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            isRepeated = false
        )
    )

    TaskHistoryScreen(
        navController = rememberNavController(),
        completedTasks = sampleTasks
    )
}
