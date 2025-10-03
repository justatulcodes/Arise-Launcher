package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.expeknow.ariselauncher.ui.theme.NavIconOutline

@Composable
fun AppIconCard(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White,
    bgColor: Color = Color.Transparent, // set to surface if you need
    outlineColor: Color = NavIconOutline,
    showNotificationDot: Boolean = false
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .background(
                color = bgColor,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(30.dp)
        )
        if (showNotificationDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(11.dp)
                    .background(Color(0xFF6AE874), RoundedCornerShape(50))
            )
        }
    }
}

@Preview
@Composable
private fun AppIconCardPreview() {
    AppIconCard(
        icon = androidx.compose.material.icons.Icons.Default.Home,
        contentDescription = "Home",
        showNotificationDot = true
    )
}
