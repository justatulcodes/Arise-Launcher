package com.expeknow.ariselauncher.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.expeknow.ariselauncher.data.model.TaskLink
import com.expeknow.ariselauncher.data.model.TaskLinkType

private fun extractYouTubeVideoId(url: String): String? {
    val patterns = listOf(
        "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([^&\\n?#]+)".toRegex(),
        "youtube\\.com/watch\\?.*v=([^&\\n?#]+)".toRegex()
    )

    for (pattern in patterns) {
        val matchResult = pattern.find(url)
        if (matchResult != null) {
            return matchResult.groupValues[1]
        }
    }
    return null
}

private fun openLink(context: Context, url: String, linkType: TaskLinkType) {
    try {
        val intent = when (linkType) {
            TaskLinkType.VIDEO -> {
                // Try to open YouTube links in YouTube app first
                if (url.contains("youtube.com") || url.contains("youtu.be")) {
                    val videoId = extractYouTubeVideoId(url)
                    if (videoId != null) {
                        // Try YouTube app first
                        val youtubeIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                        youtubeIntent.setPackage("com.google.android.youtube")

                        // Check if YouTube app is available
                        if (youtubeIntent.resolveActivity(context.packageManager) != null) {
                            youtubeIntent
                        } else {
                            // Fallback to web browser
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        }
                    } else {
                        // If we can't extract video ID, open in browser
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    }
                } else {
                    // Non-YouTube video links, open in browser
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }
            }
            TaskLinkType.ARTICLE, TaskLinkType.LINK -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            }
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: open in browser if specific app intent fails
        try {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(fallbackIntent)
        } catch (ex: Exception) {
            // Handle the case where no browser is available
            println("Error opening link: ${ex.message}")
        }
    }
}

@Composable
fun TaskDetailsScreen(
    navController: NavController,
    id: String,
    viewModel: TaskDetailsViewModel,
    state: TaskDetailsState
) {

    val theme = TaskDetailsTheme()
    val context = LocalContext.current

    // Load task when screen is first displayed
    LaunchedEffect(id) {
        viewModel.onEvent(TaskDetailsEvent.LoadTask(id))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (state.isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = theme.accent)
            }
        } else {
            state.task?.let { task ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    TaskDetailsHeader(
                        onBackClick = {
                            viewModel.onEvent(TaskDetailsEvent.NavigateBack)
                            navController.popBackStack()
                        },
                        theme = theme
                    )


                    // Points Reward Section
                    PointsRewardSection(
                        points = task.points,
                        theme = theme
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Task Details Card
                    EnhancedTaskDetailsCard(
                        task = task,
                        onToggleTask = { taskId ->
                            viewModel.onEvent(TaskDetailsEvent.ToggleTask(taskId))
                        },
                        theme = theme,
                        isEditingTitle = state.isEditingTitle,
                        isEditingDescription = state.isEditingDescription,
                        editingTitleText = state.editingTitleText,
                        editingDescriptionText = state.editingDescriptionText,
                        onStartEditingTitle = {
                            viewModel.onEvent(TaskDetailsEvent.StartEditingTitle)
                        },
                        onStartEditingDescription = {
                            viewModel.onEvent(TaskDetailsEvent.StartEditingDescription)
                        },
                        onUpdateTitleText = { text ->
                            viewModel.onEvent(TaskDetailsEvent.UpdateTitleText(text))
                        },
                        onUpdateDescriptionText = { text ->
                            viewModel.onEvent(TaskDetailsEvent.UpdateDescriptionText(text))
                        },
                        onSaveTitle = {
                            viewModel.onEvent(TaskDetailsEvent.SaveTitle)
                        },
                        onSaveDescription = {
                            viewModel.onEvent(TaskDetailsEvent.SaveDescription)
                        },
                        onCancelEditing = {
                            viewModel.onEvent(TaskDetailsEvent.CancelEditing)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resources & Links Section
                    ResourcesLinksSection(
                        links = task.relatedLinks,
                        expandedLinkId = state.expandedLinkId,
                        onExpandLink = { linkId ->
                            viewModel.onEvent(TaskDetailsEvent.ExpandLink(linkId))
                        },
                        onOpenLink = { link ->
                            openLink(context, link.url, link.type)
                        },
                        theme = theme,
                        onAddLink = {
                            viewModel.onEvent(TaskDetailsEvent.StartAddingLink)
                        },
                        onRemoveLink = { linkId ->
                            viewModel.onEvent(TaskDetailsEvent.RemoveLink(linkId))
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Completion Status Banner
                    CompletionStatusBanner(
                        isCompleted = task.isCompleted,
                        onToggleComplete = {
                            viewModel.onEvent(TaskDetailsEvent.ToggleTask(task.id))
                        },
                        theme = theme
                    )

                    // Bottom padding
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Add Link Dialog
    AddLinkDialog(
        isVisible = state.isAddingLink,
        title = state.newLinkTitle,
        url = state.newLinkUrl,
        description = state.newLinkDescription,
        selectedType = state.newLinkType,
        onTitleChange = { title ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkTitle(title))
        },
        onUrlChange = { url ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkUrl(url))
        },
        onDescriptionChange = { description ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkDescription(description))
        },
        onTypeChange = { type ->
            viewModel.onEvent(TaskDetailsEvent.UpdateNewLinkType(type))
        },
        onSave = {
            viewModel.onEvent(TaskDetailsEvent.SaveNewLink)
        },
        onCancel = {
            viewModel.onEvent(TaskDetailsEvent.CancelEditing)
        },
        theme = theme
    )
}

//@Preview(showBackground = true, backgroundColor = 0xFF000000)
//@Composable
//private fun TaskDetailsScreenPreview() {
//    TaskDetailsScreen(
//        navController = androidx.navigation.compose.rememberNavController(),
//        id = "preview-task-id",
//        viewModel = viewModel,
//        state = state
//    )
//}
