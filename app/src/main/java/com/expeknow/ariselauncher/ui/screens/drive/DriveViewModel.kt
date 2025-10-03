package com.expeknow.ariselauncher.ui.screens.drive

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DriveState(
    val currentTab: String = "quotes",
    val currentQuoteIndex: Int = 0,
    val showAddQuoteDialog: Boolean = false,
    val showEditWhyDialog: Boolean = false,
    val showAddVisionDialog: Boolean = false,
    val newQuoteText: String = "",
    val newQuoteAuthor: String = "",
    val editingWhy: WhyReason? = null,
    val motivationalQuotes: List<MotivationalQuote> = getDefaultQuotes(),
    val customQuotes: List<MotivationalQuote> = emptyList(),
    val visionCards: List<VisionCard> = getDefaultVisionCards(),
    val whyReasons: List<WhyReason> = getDefaultWhyReasons()
)

sealed class DriveEvent {
    data class SelectTab(val tab: String) : DriveEvent()
    data object NextQuote : DriveEvent()
    data class AddCustomQuote(val text: String, val author: String) : DriveEvent()
    data class DeleteCustomQuote(val quoteId: String) : DriveEvent()
    data class AddVisionCard(
        val title: String,
        val description: String,
        val imageUrl: String,
        val category: String
    ) : DriveEvent()

    data class EditVisionCard(val visionCard: VisionCard) : DriveEvent()
    data class AddWhyReason(val title: String, val description: String) : DriveEvent()
    data class EditWhyReason(val whyReason: WhyReason) : DriveEvent()
    data class DeleteWhyReason(val whyId: String) : DriveEvent()
    data object ShowAddQuoteDialog : DriveEvent()
    data object HideAddQuoteDialog : DriveEvent()
    data object ShowEditWhyDialog : DriveEvent()
    data object HideEditWhyDialog : DriveEvent()
    data object ShowAddVisionDialog : DriveEvent()
    data object HideAddVisionDialog : DriveEvent()
}

class DriveViewModel : ViewModel() {

    private val _state = MutableStateFlow(DriveState())
    val state: StateFlow<DriveState> = _state.asStateFlow()

    fun onEvent(event: DriveEvent) {
        when (event) {
            is DriveEvent.SelectTab -> {
                _state.value = _state.value.copy(currentTab = event.tab)
            }

            is DriveEvent.NextQuote -> {
                val newIndex =
                    (_state.value.currentQuoteIndex + 1) % _state.value.motivationalQuotes.size
                _state.value = _state.value.copy(currentQuoteIndex = newIndex)
            }

            is DriveEvent.AddCustomQuote -> {
                val newQuote = MotivationalQuote(
                    id = "custom_${System.currentTimeMillis()}",
                    text = event.text,
                    author = event.author,
                    category = "custom"
                )
                _state.value = _state.value.copy(
                    customQuotes = _state.value.customQuotes + newQuote,
                    showAddQuoteDialog = false,
                    newQuoteText = "",
                    newQuoteAuthor = ""
                )
            }

            is DriveEvent.DeleteCustomQuote -> {
                _state.value = _state.value.copy(
                    customQuotes = _state.value.customQuotes.filter { it.id != event.quoteId }
                )
            }

            is DriveEvent.AddVisionCard -> {
                val newVision = VisionCard(
                    id = "vision_${System.currentTimeMillis()}",
                    title = event.title,
                    description = event.description,
                    imageUrl = event.imageUrl,
                    category = event.category
                )
                _state.value = _state.value.copy(
                    visionCards = _state.value.visionCards + newVision,
                    showAddVisionDialog = false
                )
            }

            is DriveEvent.EditVisionCard -> {
                val updatedVisions = _state.value.visionCards.map { vision ->
                    if (vision.id == event.visionCard.id) event.visionCard else vision
                }
                _state.value = _state.value.copy(visionCards = updatedVisions)
            }

            is DriveEvent.AddWhyReason -> {
                val newReason = WhyReason(
                    id = "why_${System.currentTimeMillis()}",
                    title = event.title,
                    description = event.description
                )
                _state.value = _state.value.copy(
                    whyReasons = _state.value.whyReasons + newReason,
                    showEditWhyDialog = false
                )
            }

            is DriveEvent.EditWhyReason -> {
                val updatedReasons = _state.value.whyReasons.map { reason ->
                    if (reason.id == event.whyReason.id) event.whyReason else reason
                }
                _state.value = _state.value.copy(
                    whyReasons = updatedReasons,
                    showEditWhyDialog = false
                )
            }

            is DriveEvent.DeleteWhyReason -> {
                _state.value = _state.value.copy(
                    whyReasons = _state.value.whyReasons.filter { it.id != event.whyId }
                )
            }

            is DriveEvent.ShowAddQuoteDialog -> {
                _state.value = _state.value.copy(showAddQuoteDialog = true)
            }

            is DriveEvent.HideAddQuoteDialog -> {
                _state.value = _state.value.copy(
                    showAddQuoteDialog = false,
                    newQuoteText = "",
                    newQuoteAuthor = ""
                )
            }

            is DriveEvent.ShowEditWhyDialog -> {
                _state.value = _state.value.copy(showEditWhyDialog = true)
            }

            is DriveEvent.HideEditWhyDialog -> {
                _state.value = _state.value.copy(
                    showEditWhyDialog = false,
                    editingWhy = null
                )
            }

            is DriveEvent.ShowAddVisionDialog -> {
                _state.value = _state.value.copy(showAddVisionDialog = true)
            }

            is DriveEvent.HideAddVisionDialog -> {
                _state.value = _state.value.copy(showAddVisionDialog = false)
            }
        }
    }

    fun getCurrentQuote(): MotivationalQuote {
        return _state.value.motivationalQuotes[_state.value.currentQuoteIndex]
    }
}

// Default data
private fun getDefaultQuotes(): List<MotivationalQuote> = listOf(
    MotivationalQuote(
        id = "1",
        text = "Discipline is the bridge between goals and accomplishment.",
        author = "Jim Rohn",
        category = "discipline"
    ),
    MotivationalQuote(
        id = "2",
        text = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        author = "Winston Churchill",
        category = "success"
    ),
    MotivationalQuote(
        id = "3",
        text = "The only impossible journey is the one you never begin.",
        author = "Tony Robbins",
        category = "success"
    ),
    MotivationalQuote(
        id = "4",
        text = "Intelligence is not a privilege, it's a gift. And you use it for the good of mankind.",
        author = "Dr. Otto Octavius",
        category = "intelligence"
    ),
    MotivationalQuote(
        id = "5",
        text = "Strength does not come from physical capacity. It comes from an indomitable will.",
        author = "Mahatma Gandhi",
        category = "strength"
    ),
    MotivationalQuote(
        id = "6",
        text = "The way to get started is to quit talking and begin doing.",
        author = "Walt Disney",
        category = "discipline"
    )
)

private fun getDefaultVisionCards(): List<VisionCard> = listOf(
    VisionCard(
        id = "1",
        title = "Master Advanced Mathematics",
        description = "Become proficient in calculus, linear algebra, and statistics",
        imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=400&h=300&fit=crop",
        category = "intelligence"
    ),
    VisionCard(
        id = "2",
        title = "Achieve Peak Physical Form",
        description = "Build strength, endurance, and maintain optimal health",
        imageUrl = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&h=300&fit=crop",
        category = "physical"
    ),
    VisionCard(
        id = "3",
        title = "Financial Independence",
        description = "Build multiple income streams and investment portfolio",
        imageUrl = "https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=400&h=300&fit=crop",
        category = "wealth"
    )
)

private fun getDefaultWhyReasons(): List<WhyReason> = listOf(
    WhyReason(
        id = "1",
        title = "For Your Future Self",
        description = "Every task you complete today is an investment in the person you're becoming tomorrow."
    ),
    WhyReason(
        id = "2",
        title = "For Your Dreams",
        description = "The goals that seem impossible today will be your reality with consistent action."
    ),
    WhyReason(
        id = "3",
        title = "For Your Legacy",
        description = "Discipline today creates the freedom and impact you want to leave behind."
    )
)