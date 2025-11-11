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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            item {
                MvpStatsContent(
                    statsUi = state.mvpStats,
                    currentRank = currentRank
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
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