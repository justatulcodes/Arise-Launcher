package com.expeknow.ariselauncher.ui.screens.drive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expeknow.ariselauncher.data.model.DriveItem
import com.expeknow.ariselauncher.data.model.DriveItemType
import com.expeknow.ariselauncher.data.repository.DriveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriveState(
    val driveItems: List<DriveItem> = emptyList(),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val selectedItemType: DriveItemType = DriveItemType.QUOTE,
    val editingItem: DriveItem? = null
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
    data object ShowAddDialog : DriveEvent()
    data object HideAddDialog : DriveEvent()
    data class StartEditItem(val item: DriveItem) : DriveEvent()
    data object CancelEdit : DriveEvent()
}

@HiltViewModel
class DriveViewModel @Inject constructor(
    private val repository: DriveRepository
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
                    isLoading = false
                )
            }
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
        }
    }
}
