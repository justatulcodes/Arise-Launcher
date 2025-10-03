package com.expeknow.ariselauncher.ui.screens.drive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview

data class DriveTheme(
    val background: Color = Color.Black,
    val surface: Color = Color(0xFF1F1F1F),
    val textPrimary: Color = Color.White,
    val textSecondary: Color = Color(0xFF9CA3AF),
    val border: Color = Color.White.copy(alpha = 0.2f),
    val accent: Color = Color.White
)

@Composable
fun DriveHeader(
    currentTab: String,
    onTabSelect: (String) -> Unit,
    theme: DriveTheme
) {
    Column(
        modifier = Modifier
            .background(theme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "YOUR DRIVE",
            style = MaterialTheme.typography.titleLarge,
            color = theme.textPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = theme.border,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(theme.surface)
        ) {
            listOf("quotes", "goals", "reminders").forEach { tab ->
                val isSelected = tab == currentTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .background(
                            if (isSelected) theme.accent else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onTabSelect(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.Black else theme.textPrimary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun DriveHeaderPreview() {
    DriveHeader(
        currentTab = "quotes",
        onTabSelect = {},
        theme = DriveTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun FeaturedQuoteCardPreview() {
    val sampleQuote = MotivationalQuote(
        id = "1",
        text = "The only impossible journey is the one you never begin",
        author = "Tony Robbins",
        category = "motivation"
    )

    FeaturedQuoteCard(
        quote = sampleQuote,
        onNextQuote = {},
        theme = DriveTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CategoryQuoteCardPreview() {
    CategoryQuoteCard(
        icon = Icons.Filled.Lightbulb,
        category = "DISCIPLINE",
        quote = "Self-discipline is the magic power that makes you virtually unstoppable.",
        theme = DriveTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun EmergencyMotivationCardPreview() {
    EmergencyMotivationCard(theme = DriveTheme())
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun VisionBoardSectionPreview() {
    val sampleVisionCards = listOf(
        VisionCard(
            id = "1",
            title = "Dream Home",
            description = "My ideal living space",
            imageUrl = "https://example.com/image.jpg",
            category = "wealth"
        )
    )

    VisionBoardSection(
        visionCards = sampleVisionCards,
        onAddVision = {},
        onEditVision = {},
        theme = DriveTheme()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WhyRemindersSectionPreview() {
    val sampleWhyReasons = listOf(
        WhyReason(
            id = "1",
            title = "Family",
            description = "To provide a better life for my loved ones"
        ),
        WhyReason(
            id = "2",
            title = "Freedom",
            description = "To have the freedom to choose my path"
        )
    )

    WhyRemindersSection(
        whyReasons = sampleWhyReasons,
        onEditWhy = {},
        onAddWhy = {},
        onDeleteWhy = {},
        theme = DriveTheme()
    )
}

@Composable
fun FeaturedQuoteCard(
    quote: MotivationalQuote,
    onNextQuote: () -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = theme.border,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                theme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = "Quote",
                tint = theme.textPrimary.copy(alpha = 0.6f),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"${quote.text}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = theme.textPrimary,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— ${quote.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.textPrimary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNextQuote,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = theme.textPrimary
                ),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = theme.border,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Text("Next Quote")
            }
        }
    }
}

@Composable
fun CategoryQuoteCard(
    icon: ImageVector,
    category: String,
    quote: String,
    theme: DriveTheme = DriveTheme(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                theme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = category,
                    tint = theme.textPrimary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = theme.textPrimary
                )
            }

            Text(
                text = quote,
                style = MaterialTheme.typography.bodySmall,
                color = theme.textPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CustomQuotesSection(
    customQuotes: List<MotivationalQuote>,
    onAddQuote: () -> Unit,
    onDeleteQuote: (String) -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                theme.surface,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR CUSTOM QUOTES",
                    style = MaterialTheme.typography.labelLarge,
                    color = theme.textPrimary
                )

                IconButton(onClick = onAddQuote) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Quote",
                        tint = theme.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (customQuotes.isEmpty()) {
                EmptyCustomQuotesPlaceholder(theme)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    customQuotes.forEach { quote ->
                        CustomQuoteItem(
                            quote = quote,
                            onDelete = { onDeleteQuote(quote.id) },
                            theme = theme
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VisionBoardSection(
    visionCards: List<VisionCard>,
    onAddVision: () -> Unit,
    onEditVision: (VisionCard) -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = theme.border,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                theme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR VISION BOARD",
                    style = MaterialTheme.typography.labelLarge,
                    color = theme.textPrimary
                )

                IconButton(onClick = onAddVision) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Vision",
                        tint = theme.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visionCards.forEach { card ->
                    VisionCardItem(
                        visionCard = card,
                        onClick = { onEditVision(card) },
                        theme = theme
                    )
                }
            }
        }
    }
}

@Composable
fun WhyRemindersSection(
    whyReasons: List<WhyReason>,
    onEditWhy: () -> Unit,
    onAddWhy: () -> Unit,
    onDeleteWhy: (String) -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = theme.border,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                theme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Heart",
                tint = theme.textPrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "REMEMBER YOUR WHY",
                    style = MaterialTheme.typography.titleMedium,
                    color = theme.textPrimary
                )

                IconButton(onClick = onEditWhy) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Why",
                        tint = theme.textPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                whyReasons.forEach { reason ->
                    WhyReasonItem(
                        whyReason = reason,
                        onEdit = { /* Handle edit */ },
                        onDelete = { onDeleteWhy(reason.id) },
                        theme = theme
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddWhy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = theme.textPrimary.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = theme.border,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Your Why")
            }
        }
    }
}

@Composable
fun EmergencyMotivationCard(theme: DriveTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFE57373).copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                Color(0xFFE57373).copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = "Target",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "WHEN YOU WANT TO QUIT",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFE57373)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val motivations = listOf(
                    "Remember: Discipline weighs ounces, regret weighs tons",
                    "You've already started - don't waste that progress",
                    "Future you is counting on present you",
                    "Every elite person felt exactly how you feel right now"
                )

                motivations.forEach { motivation ->
                    Text(
                        text = "• $motivation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = theme.textPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCustomQuotesPlaceholder(theme: DriveTheme) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = "No Quotes",
                tint = theme.textPrimary.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No custom quotes yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = theme.textPrimary.copy(alpha = 0.4f)
            )

            Text(
                text = "Add your favorite motivational quotes!",
                style = MaterialTheme.typography.bodySmall,
                color = theme.textPrimary.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun CustomQuoteItem(
    quote: MotivationalQuote,
    onDelete: () -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.border.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.textPrimary,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = theme.textPrimary.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun VisionCardItem(
    visionCard: VisionCard,
    onClick: () -> Unit,
    theme: DriveTheme
) {
    val categoryColor = when (visionCard.category) {
        "intelligence" -> Color(0xFF64B5F6)
        "physical" -> Color(0xFFFFB74D)
        "wealth" -> Color(0xFF81C784)
        else -> theme.textPrimary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = categoryColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                categoryColor.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = when (visionCard.category) {
                        "intelligence" -> Icons.Filled.Psychology
                        "physical" -> Icons.Filled.Bolt
                        "wealth" -> Icons.Filled.AttachMoney
                        else -> Icons.Filled.Lightbulb
                    },
                    contentDescription = visionCard.category,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = visionCard.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = theme.textPrimary
                    )

                    Text(
                        text = visionCard.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = theme.textPrimary.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = visionCard.imageUrl,
                    contentDescription = visionCard.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = theme.border
                        )
                    ) {
                        Text(
                            text = "Click to Edit",
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.textPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WhyReasonItem(
    whyReason: WhyReason,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    theme: DriveTheme
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = theme.border,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                theme.textPrimary.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = whyReason.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = theme.textPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = whyReason.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.textPrimary.copy(alpha = 0.7f)
                )
            }

            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = theme.textPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE57373).copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}