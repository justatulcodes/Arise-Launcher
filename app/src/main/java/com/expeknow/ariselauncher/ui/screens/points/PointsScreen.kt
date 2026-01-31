package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsScreen(
    navController: NavController,
    viewModel: PointsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(navController) {
        viewModel.setNavController(navController)
    }

    // Calculate current rank
    val currentRank = state.debugCurrentRank ?: ranks.find { rank ->
        state.currentPoints >= rank.minPoints && state.currentPoints <= rank.maxPoints
    } ?: ranks[0]

    // Determine tabs based on mode
    val tabs = if (state.isTunnelVisionMode) {
        listOf("Focus Tasks", "Personal Tasks")
    } else {
        listOf("Personal Tasks")
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        showHeatmap()
    }



//        if (tabs.size > 1) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp, vertical = 4.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color(0xFF1F1F1F))
//                    .padding(4.dp),
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                tabs.forEachIndexed { index, title ->
//                    val isSelected = pagerState.currentPage == index
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .clip(RoundedCornerShape(6.dp))
//                            .background(
//                                if (isSelected) currentRank.colors.accent
//                                else Color.Transparent
//                            )
//                            .clickable {
//                                coroutineScope.launch {
//                                    pagerState.animateScrollToPage(index)
//                                }
//                            }
//                            .padding(vertical = 8.dp, horizontal = 12.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = title,
//                            style = MaterialTheme.typography.bodyMedium.copy(
//                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
//                                fontSize = 13.sp,
//                                letterSpacing = 0.3.sp
//                            ),
//                            color = if (isSelected) Color.Black else Color(0xFF9CA3AF)
//                        )
//                    }
//                }
//            }
//        } else {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Black)
//                    .padding(vertical = 12.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = tabs[0],
//                    style = MaterialTheme.typography.titleSmall.copy(
//                        fontWeight = FontWeight.Bold,
//                        letterSpacing = 0.5.sp
//                    ),
//                    color = currentRank.colors.accent
//                )
//            }
//        }
//
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier.weight(1f)
//        ) { page ->
//            LazyColumn(
//                modifier = Modifier.fillMaxSize()
//            ) {
//                item {
//                    when {
//                        state.isTunnelVisionMode && page == 0 -> {
//                            FocusTasksStatsTab(
//                                statsUi = state.mvpStats,
//                                currentRank = currentRank
//                            )
//                        }
//                        state.isTunnelVisionMode && page == 1 -> {
//                            PersonalTasksStatsTab(
//                                statsUi = state.mvpStats,
//                                currentRank = currentRank
//                            )
//                        }
//                        !state.isTunnelVisionMode && page == 0 -> {
//                            PersonalTasksStatsTab(
//                                statsUi = state.mvpStats,
//                                currentRank = currentRank
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
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
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = categoryName,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        for (weekIndex in heatmapData.indices) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(14)
            ) {
                items(heatmapData[weekIndex].size) { dayIndex ->
                    val taskCount = heatmapData[weekIndex][dayIndex]
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
                                .height(26.dp)
                                .width(26.dp)
                                .padding((1.5).dp)
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
private fun showHeatmap() {
    // Generate random data for each category
    val peopleData = remember { generateRandomHeatmapData() }
    val opportunityData = remember { generateRandomHeatmapData() }
    val skillsData = remember { generateRandomHeatmapData() }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(vertical = 8.dp)
    ) {
        CategoryHeatmap(
            categoryName = "People Interactions",
            primaryColor = PeopleColor,
            heatmapData = peopleData
        )

        CategoryHeatmap(
            categoryName = "Opportunities",
            primaryColor = OpportunityColor,
            heatmapData = opportunityData
        )

        CategoryHeatmap(
            categoryName = "Skills Development",
            primaryColor = SkillsColor,
            heatmapData = skillsData
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PointsScreenPreview() {
    PointsScreen(
        navController = rememberNavController(),
        viewModel = viewModel()
    )
}