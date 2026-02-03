package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expeknow.ariselauncher.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsScreenViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(navController) {
        viewModel.setNavController(navController)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Stats",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text(
            text = "Your focus activity over time",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
        )

        ShowHeatmap(
            peopleData = state.peopleHeatmap.weeklyData,
            opportunityData = state.opportunityHeatmap.weeklyData,
            skillsData = state.skillsHeatmap.weeklyData
        )
    }



}

private val PeopleColor = Color(0xFF60A5FA)
private val OpportunityColor = Color(0xFFFB923C)
private val SkillsColor = Color(0xFF4ADE80)

private fun generateRandomHeatmapData(): List<List<Int>> {
    return (1..7).map {
        (1..14).map { kotlin.random.Random.nextInt(0, 6) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CategoryHeatmap(
    categoryName: String,
    primaryColor: Color,
    heatmapData: List<List<Int>>
) {
    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = categoryName,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Row 0 (top) = oldest week, Last row (bottom) = most recent week
        // Column 0 (left) = oldest day in week, Last column (right) = most recent day
        val reversedData = heatmapData.reversed().map { it.reversed() }

        Column {
            for (weekIndex in reversedData.indices) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    for (dayIndex in reversedData[weekIndex].indices) {
                        val taskCount = reversedData[weekIndex][dayIndex]
                        val cellColor = getHeatmapCellColor(primaryColor, taskCount)

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip { Text("$taskCount tasks completed") }
                            },
                            state = rememberTooltipState()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .padding(1.5.dp)
                                    .background(
                                        shape = RoundedCornerShape(6.dp),
                                        color = cellColor
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (taskCount > 0) {
                                    Text(
                                        text = "$taskCount",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getHeatmapCellColor(baseColor: Color, taskCount: Int): Color {
    return when (taskCount) {
        0 -> Color(0xFF1F1F1F) // Empty/dark cell
        1 -> baseColor.copy(alpha = 0.2f)
        2 -> baseColor.copy(alpha = 0.4f)
        3 -> baseColor.copy(alpha = 0.6f)
        4 -> baseColor.copy(alpha = 0.8f)
        else -> baseColor // 5+ tasks = full color
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShowHeatmap(
    peopleData: List<List<Int>>,
    opportunityData: List<List<Int>>,
    skillsData: List<List<Int>>
) {
    // Use provided data, or fallback to empty if not available
    val safePeopleData = peopleData.ifEmpty { generateEmptyHeatmapData() }
    val safeOpportunityData = opportunityData.ifEmpty { generateEmptyHeatmapData() }
    val safeSkillsData = skillsData.ifEmpty { generateEmptyHeatmapData() }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        CategoryHeatmap(
            categoryName = "People Interactions",
            primaryColor = PeopleColor,
            heatmapData = safePeopleData
        )

        CategoryHeatmap(
            categoryName = "Opportunities",
            primaryColor = OpportunityColor,
            heatmapData = safeOpportunityData
        )

        CategoryHeatmap(
            categoryName = "Skills Development",
            primaryColor = SkillsColor,
            heatmapData = safeSkillsData
        )
    }
}

private fun generateEmptyHeatmapData(): List<List<Int>> {
    return (1..7).map {
        (1..14).map { 0 }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ShowHeatmapPreview() {
    // Generate random data for preview
    val peopleData = remember { generateRandomHeatmapData() }
    val opportunityData = remember { generateRandomHeatmapData() }
    val skillsData = remember { generateRandomHeatmapData() }

    ShowHeatmap(
        peopleData = peopleData,
        opportunityData = opportunityData,
        skillsData = skillsData
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun StatsScreenPreview() {
    StatsScreen(
        navController = rememberNavController(),
        viewModel = viewModel()
    )
}