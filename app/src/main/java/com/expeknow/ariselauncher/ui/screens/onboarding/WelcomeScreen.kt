package com.expeknow.ariselauncher.ui.screens.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.expeknow.ariselauncher.R
import com.expeknow.ariselauncher.ui.theme.AccentGreen
import com.expeknow.ariselauncher.ui.theme.BannerTextGray
import com.expeknow.ariselauncher.ui.theme.TaskTitle

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "welcome_fade_in"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // App Logo
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Arise Launcher Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            // App Name
            Text(
                text = "Arise Launcher",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Rise Above Distractions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AccentGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Description
            Text(
                text = "Arise is your productivity companion that helps you focus on what matters most. Track your app usage, set meaningful goals, and transform your screen time into achievements.",
                fontSize = 16.sp,
                color = BannerTextGray,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature highlights
            FeatureItem(
                text = "Track screen time and app usage patterns"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(
                text = "Set focus goals and build productive habits"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(
                text = "Earn points and level up your productivity"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureItem(
                text = "Stay motivated with real-time insights"
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = {
                    navController.navigate(com.expeknow.ariselauncher.ui.navigation.Screen.PermissionOnboarding.route) {
                        popUpTo(com.expeknow.ariselauncher.ui.navigation.Screen.Welcome.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            fontSize = 16.sp,
            color = AccentGreen,
            modifier = Modifier.padding(end = 12.dp, top = 2.dp)
        )

        Text(
            text = text,
            fontSize = 15.sp,
            color = TaskTitle,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
