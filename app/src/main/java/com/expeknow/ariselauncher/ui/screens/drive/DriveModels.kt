package com.expeknow.ariselauncher.ui.screens.drive

import android.net.Uri
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType

data class DriveState(
    val driveItems: List<DriveItem> = emptyList(),
    val filteredItems: List<DriveItem> = emptyList(),
    val selectedFilter: DriveItemType? = null,
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val selectedItemType: DriveItemType = DriveItemType.QUOTE,
    val editingItem: DriveItem? = null,
    val isSavingImage: Boolean = false
)

sealed class DriveEvent {
    data class AddItem(
        val type: DriveItemType,
        val content: String,
        val title: String = "",
        val author: String = "",
        val description: String = ""
    ) : DriveEvent()

    data class UpdateItem(val item: DriveItem) : DriveEvent()
    data class DeleteItem(val itemId: String) : DriveEvent()
    data class SelectItemType(val type: DriveItemType) : DriveEvent()
    data class SelectFilter(val type: DriveItemType?) : DriveEvent()
    data object ShowAddDialog : DriveEvent()
    data object HideAddDialog : DriveEvent()
    data class StartEditItem(val item: DriveItem) : DriveEvent()
    data object CancelEdit : DriveEvent()
    data class OpenVideo(val videoUrl: String) : DriveEvent()
    data class SaveImageFromUri(val uri: Uri, val title: String, val description: String) : DriveEvent()
}