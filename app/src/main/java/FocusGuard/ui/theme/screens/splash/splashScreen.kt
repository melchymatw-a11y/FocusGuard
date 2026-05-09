package FocusGuard.ui.theme.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import FocusGuard.navigation.ROUTE_LOGIN
import com.example.focusguard.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // Your signature background
    val subtitleColor = Color(0xFFCC00FF).copy(alpha = 0.9f) // Electric purple for text

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(ROUTE_LOGIN) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepPurpleBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // --- ORIGINAL SHIELD IMAGE ---
            // Removed colorFilter so the PNG's natural gradients and shadows show up
            Image(
                painter = painterResource(id = R.drawable.shield), // Matches your filename 'shield.png'
                contentDescription = "FocusGuard Shield",
                modifier = Modifier.size(180.dp) // Slightly larger to show off the detail
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "FocusGuard",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "STAY PRODUCTIVE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = subtitleColor,
                letterSpacing = 4.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}