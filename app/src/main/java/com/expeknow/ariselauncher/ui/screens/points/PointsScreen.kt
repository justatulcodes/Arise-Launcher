package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expeknow.ariselauncher.data.model.*

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

    val nextRank = ranks.find { rank -> rank.minPoints > currentRank.minPoints }

    val progressToNext = nextRank?.let {
        ((state.currentPoints.toFloat() - currentRank.minPoints) / (it.minPoints - currentRank.minPoints)) * 100f
    } ?: 100f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        /*
        PointsHeader(
            selectedTabIndex = state.selectedTabIndex,
            onTabSelect = { index: Int -> viewModel.onEvent(PointsEvent.SelectTab(index)) },
            currentRank = currentRank
        )
        */

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item {
                TasksContent(
                    currentRank = currentRank,
                    taskStats = state.taskStats,
                    onNavigateToTaskHistory = {
                        viewModel.onEvent(PointsEvent.NavigateToTaskHistory)
                    }
                )
            }


            item {
                StatsGrid(
                    totalEarned = state.totalEarned,
                    totalBurned = state.totalBurned,
                    currentRank = currentRank
                )
            }

//            item {
//                RecentActivityCard(
//                    activities = state.activities,
//                    currentRank = currentRank
//                )
//            }
//
//
//
//            item {
//                PointsGrowthChart(
//                    pointsHistory = state.pointsHistory,
//                    currentRank = currentRank
//                )
//            }

//            item {
//                PointSystemCard(currentRank = currentRank)
//            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// Keep the original OverviewTabContent commented out for reference
/*
@Composable
private fun OverviewTabContent(
    currentPoints: Int,
    currentRank: Rank,
    nextRank: Rank?,
    progressToNext: Float,
    pointsHistory: List<PointsHistory>,
    totalEarned: Int,
    totalBurned: Int,
    activities: List<PointActivity>
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CurrentStatusCard(
            currentPoints = currentPoints,
            currentRank = currentRank,
            nextRank = nextRank,
            progressToNext = progressToNext
        )

        PointsGrowthChart(
            pointsHistory = pointsHistory,
            currentRank = currentRank
        )

        StatsGrid(
            totalEarned = totalEarned,
            totalBurned = totalBurned,
            currentRank = currentRank
        )

        PointSystemCard(currentRank = currentRank)

        RecentActivityCard(
            activities = activities,
            currentRank = currentRank
        )
    }
}
*/

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PointsScreenPreview() {
    PointsScreen(
        navController = rememberNavController(),
        viewModel = viewModel()
    )
}