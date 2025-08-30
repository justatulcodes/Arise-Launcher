package com.expeknow.ariselauncher.ui.screens.drive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Data classes - keeping here to avoid duplication issues with the existing code
data class MotivationalQuote(
    val id: String,
    val text: String,
    val author: String,
    val category: String
)

data class VisionCard(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String
)

data class WhyReason(
    val id: String,
    val title: String,
    val description: String
)

@Composable
fun DriveScreen(
    navController: NavController,
    viewModel: DriveViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = DriveTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Header with tabs
        DriveHeader(
            currentTab = state.currentTab,
            onTabSelect = { tab: String ->
                viewModel.onEvent(DriveEvent.SelectTab(tab))
            },
            theme = theme
        )

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (state.currentTab) {
                "quotes" -> {
                    // Featured Quote
                    item {
                        FeaturedQuoteCard(
                            quote = viewModel.getCurrentQuote(),
                            onNextQuote = {
                                viewModel.onEvent(DriveEvent.NextQuote)
                            },
                            theme = theme
                        )
                    }

                    // Quote Categories
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CategoryQuoteCard(
                                icon = Icons.Filled.Lightbulb,
                                category = "DISCIPLINE",
                                quote = "Self-discipline is the magic power that makes you virtually unstoppable.",
                                theme = theme,
                                modifier = Modifier.weight(1f)
                            )

                            CategoryQuoteCard(
                                icon = Icons.Filled.Star,
                                category = "SUCCESS",
                                quote = "Success is where preparation and opportunity meet.",
                                theme = theme,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Custom Quotes Section
                    item {
                        CustomQuotesSection(
                            customQuotes = state.customQuotes,
                            onAddQuote = {
                                viewModel.onEvent(DriveEvent.ShowAddQuoteDialog)
                            },
                            onDeleteQuote = { quoteId: String ->
                                viewModel.onEvent(DriveEvent.DeleteCustomQuote(quoteId))
                            },
                            theme = theme
                        )
                    }
                }

                "goals" -> {
                    // Vision Board
                    item {
                        VisionBoardSection(
                            visionCards = state.visionCards,
                            onAddVision = {
                                viewModel.onEvent(DriveEvent.ShowAddVisionDialog)
                            },
                            onEditVision = { visionCard: VisionCard ->
                                viewModel.onEvent(DriveEvent.EditVisionCard(visionCard))
                            },
                            theme = theme
                        )
                    }
                }

                "reminders" -> {
                    // Your Why
                    item {
                        WhyRemindersSection(
                            whyReasons = state.whyReasons,
                            onEditWhy = {
                                viewModel.onEvent(DriveEvent.ShowEditWhyDialog)
                            },
                            onAddWhy = {
                                // Create new why reason
                                viewModel.onEvent(DriveEvent.AddWhyReason("New Why", "Description"))
                            },
                            onDeleteWhy = { whyId: String ->
                                viewModel.onEvent(DriveEvent.DeleteWhyReason(whyId))
                            },
                            theme = theme
                        )
                    }

                    // Emergency Motivation
                    item {
                        EmergencyMotivationCard(theme = theme)
                    }
                }
            }
        }
    }

    // TODO: Add dialogs for adding/editing content when state shows them
    // This would include:
    // - Add Quote Dialog (state.showAddQuoteDialog)
    // - Edit Why Dialog (state.showEditWhyDialog) 
    // - Add Vision Dialog (state.showAddVisionDialog)
}