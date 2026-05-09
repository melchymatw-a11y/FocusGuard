package FocusGuard.ui.theme.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import FocusGuard.navigation.ROUTE_LOGIN
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8

    LaunchedEffect(Unit) {
        delay(2000) // 2-second delay for the splash effect
        navController.navigate(ROUTE_LOGIN) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepPurpleBg), // Applied Deep Purple Background
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FocusGuard",
                fontSize = 48.sp, // Slightly larger for impact
                fontWeight = FontWeight.Black, // Extra bold weight
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "STAY PRODUCTIVE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 4.sp // Spaced out for a modern aesthetic
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}