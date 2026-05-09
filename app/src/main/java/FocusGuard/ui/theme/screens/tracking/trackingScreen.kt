package FocusGuard.ui.theme.screens.tracking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(navController: NavController, goalName: String?) {
    val context = LocalContext.current

    var isTracking by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableStateOf(0) }

    // Consistent Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8
    val primaryPurpleBtn = Color(0xFF7C3AED) // #7C3AED
    val accentCyan = Color(0xFF06B6D4) // #06B6D4
    val deleteRed = Color(0xFFEF4444) // #EF4444

    LaunchedEffect(isTracking) {
        if (isTracking) {
            while (true) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    val minutes = secondsElapsed / 60
    val seconds = secondsElapsed % 60
    val timeDisplay = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        containerColor = deepPurpleBg, // Applied Deep Purple Background
        topBar = {
            TopAppBar(
                title = { Text("FOCUS SESSION", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // White TopBar
                    titleContentColor = deepPurpleBg // Purple Text
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Focusing on:",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = goalName ?: "Active Goal",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White // Text on purple is white
            )

            Spacer(modifier = Modifier.height(48.dp))

            // The Ticking Timer Card
            Surface(
                modifier = Modifier.size(280.dp),
                shape = RoundedCornerShape(140.dp), // Circular Surface
                color = Color.White, // White background for the card/circle
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = (secondsElapsed % 60) / 60f,
                        modifier = Modifier.size(240.dp),
                        strokeWidth = 10.dp,
                        color = if (isTracking) accentCyan else Color.LightGray // Cyan ring when active
                    )
                    Text(
                        text = timeDisplay,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black // Dark text inside white card
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Control Button
            Button(
                onClick = {
                    if (!isTracking) {
                        isTracking = true
                    } else {
                        isTracking = false
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val database = FirebaseDatabase.getInstance().reference

                        val sessionData = mapOf(
                            "goalname" to goalName,
                            "timedisplay" to timeDisplay,
                            "secondselapsed" to secondsElapsed,
                            "timestamp" to System.currentTimeMillis()
                        )
                        database.child("sessions")
                            .child(userId!!)
                            .push()
                            .setValue(sessionData)
                        Toast.makeText(context, "Session Saved: $timeDisplay", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTracking) deleteRed else primaryPurpleBtn // Red to stop, Purple to start
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(0.75f)
            ) {
                Icon(
                    imageVector = if (isTracking) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (isTracking) "FINISH SESSION" else "START TRACKING",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}