package com.expeknow.ariselauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.expeknow.ariselauncher.ui.navigation.Screen
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.theme.NavBarBg
import com.expeknow.ariselauncher.ui.theme.NavIconOutline

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBarBg)
            .padding(vertical = 8.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                icon = Icons.Filled.CheckBox,
                label = "FOCUS",
                isSelected = currentRoute == Screen.Focus.route,
                onClick = {
                    navController.navigate(Screen.Focus.route) {
                        popUpTo(Screen.Focus.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            NavBarItem(
                icon = Icons.Filled.EmojiEvents,
                label = "POINTS",
                isSelected = currentRoute == Screen.Points.route,
                onClick = {
                    navController.navigate(Screen.Points.route) {
                        popUpTo(Screen.Focus.route)
                        launchSingleTop = true
                    }
                }
            )
            NavBarItem(
                icon = Icons.Filled.Dashboard,
                label = "DRIVE",
                isSelected = currentRoute == Screen.Drive.route,
                onClick = {
                    navController.navigate(Screen.Drive.route) {
                        popUpTo(Screen.Focus.route)
                        launchSingleTop = true
                    }
                }
            )
            NavBarItem(
                icon = Icons.Filled.GridView,
                label = "APPS",
                isSelected = currentRoute == Screen.Apps.route,
                showNotificationDot = true,
                onClick = {
                    navController.navigate(Screen.Apps.route) {
                        popUpTo(Screen.Focus.route)
                        launchSingleTop = true
                    }
                }
            )
            NavBarItem(
                icon = Icons.Filled.ControlPoint,
                label = "CTRL",
                isSelected = currentRoute == Screen.Ctrl.route,
                onClick = {
                    navController.navigate(Screen.Ctrl.route) {
                        popUpTo(Screen.Focus.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    showNotificationDot: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        AppIconCard(
            icon = icon,
            contentDescription = label,
            bgColor = if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent,
            outlineColor = NavIconOutline,
            showNotificationDot = showNotificationDot
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else BannerTextGray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}