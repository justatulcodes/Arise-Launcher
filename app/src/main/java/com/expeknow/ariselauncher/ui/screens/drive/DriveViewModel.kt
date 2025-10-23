package com.expeknow.ariselauncher.ui.screens.drive

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import com.expeknow.ariselauncher.data.repository.interfaces.DriveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriveViewModel @Inject constructor(
    private val repository: DriveRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(DriveState())
    val state: StateFlow<DriveState> = _state.asStateFlow()

    init {
        loadDriveItems()
    }

    private fun loadDriveItems() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getAllDriveItems().collect { items ->
                _state.value = _state.value.copy(
                    driveItems = items,
                    filteredItems = filterItems(items, _state.value.selectedFilter),
                    isLoading = false
                )
            }
        }
    }

    private fun filterItems(items: List<DriveItem>, filter: DriveItemType?): List<DriveItem> {
        return if (filter == null) {
            items
        } else {
            items.filter { it.type == filter }
        }
    }

    fun onEvent(event: DriveEvent) {
        when (event) {
            is DriveEvent.AddItem -> {
                viewModelScope.launch {
                    val newItem = DriveItem(
                        type = event.type,
                        content = event.content,
                        title = event.title,
                        author = event.author,
                        description = event.description
                    )
                    repository.insertDriveItem(newItem)
                    _state.value = _state.value.copy(showAddDialog = false)
                }
            }

            is DriveEvent.UpdateItem -> {
                viewModelScope.launch {
                    repository.updateDriveItem(event.item)
                    _state.value = _state.value.copy(
                        editingItem = null,
                        showAddDialog = false
                    )
                }
            }

            is DriveEvent.DeleteItem -> {
                viewModelScope.launch {
                    repository.deleteDriveItemById(event.itemId)
                }
            }

            is DriveEvent.SelectItemType -> {
                _state.value = _state.value.copy(selectedItemType = event.type)
            }

            is DriveEvent.SelectFilter -> {
                val filteredItems = filterItems(_state.value.driveItems, event.type)
                _state.value = _state.value.copy(
                    selectedFilter = event.type,
                    filteredItems = filteredItems
                )
            }

            is DriveEvent.ShowAddDialog -> {
                _state.value = _state.value.copy(showAddDialog = true, editingItem = null)
            }

            is DriveEvent.HideAddDialog -> {
                _state.value = _state.value.copy(showAddDialog = false, editingItem = null)
            }

            is DriveEvent.StartEditItem -> {
                _state.value = _state.value.copy(
                    editingItem = event.item,
                    showAddDialog = true,
                    selectedItemType = event.item.type
                )
            }

            is DriveEvent.CancelEdit -> {
                _state.value = _state.value.copy(editingItem = null, showAddDialog = false)
            }

            is DriveEvent.OpenVideo -> {
                repository.openVideo(context, event.videoUrl)
            }

            is DriveEvent.SaveImageFromUri -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isSavingImage = true)
                    val savedPath = repository.saveImageFromUri(context, event.uri)
                    if (savedPath != null) {
                        val newItem = DriveItem(
                            type = DriveItemType.IMAGE,
                            content = savedPath,
                            title = event.title,
                            description = event.description
                        )
                        repository.insertDriveItem(newItem)
                        _state.value = _state.value.copy(
                            showAddDialog = false,
                            isSavingImage = false
                        )
                    } else {
                        _state.value = _state.value.copy(isSavingImage = false)
                    }
                }
            }
        }
    }
}
