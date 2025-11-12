package com.expeknow.ariselauncher.ui.screens.points

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch

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
        // Tab Row
        if (tabs.size > 1) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Black,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        Box(
                            Modifier
                                .width(tabPositions[pagerState.currentPage].width)
                                .offset(x = tabPositions[pagerState.currentPage].left)
                                .height(3.dp)
                                .background(currentRank.colors.accent)
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title.uppercase(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (pagerState.currentPage == index)
                                        FontWeight.Bold else FontWeight.Normal,
                                    letterSpacing = 0.5.sp
                                ),
                                color = if (pagerState.currentPage == index)
                                    currentRank.colors.accent else Color(0xFF9CA3AF)
                            )
                        },
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        } else {
            // Single tab header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tabs[0].uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = currentRank.colors.accent
                )
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    when {
                        state.isTunnelVisionMode && page == 0 -> {
                            // Focus Tasks Tab
                            FocusTasksStatsTab(
                                statsUi = state.mvpStats,
                                currentRank = currentRank
                            )
                        }
                        state.isTunnelVisionMode && page == 1 -> {
                            // Personal Tasks Tab (in tunnel vision mode)
                            PersonalTasksStatsTab(
                                statsUi = state.mvpStats,
                                currentRank = currentRank
                            )
                        }
                        !state.isTunnelVisionMode && page == 0 -> {
                            // Personal Tasks Tab (in normal mode)
                            PersonalTasksStatsTab(
                                statsUi = state.mvpStats,
                                currentRank = currentRank
                            )
                        }
                    }
                }
            }
        }
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