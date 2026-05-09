package FocusGuard.ui.theme.screens.goals

import androidx.compose.foundation.Image // Use Image for custom graphics
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Required to load drawable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import FocusGuard.data.GoalsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.focusguard.R // Ensure this matches your package name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateGoalScreen(navController: NavController, goalId: String) {

    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val context = LocalContext.current
    val goalsViewModel: GoalsViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8)
    val primaryPurpleBtn = Color(0xFF7C3AED)
    val accentCyan = Color(0xFF06B6D4)

    LaunchedEffect(Unit) {
        goalsViewModel.fetchSingleGoal(userId, goalId) { goal ->
            goal?.let {
                title = it.title
                duration = it.durationHours
            }
        }
    }

    Scaffold(
        containerColor = deepPurpleBg,
        topBar = {
            TopAppBar(
                title = { Text("EDIT GOAL", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = deepPurpleBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = deepPurpleBg
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- CUSTOM IMAGE LOGO START ---
            // Ensure you have a file named 'shield_logo' in res/drawable
            Image(
                painter = painterResource(id = R.drawable.shield),
                contentDescription = "FocusGuard Logo",
                modifier = Modifier.size(80.dp)
            )
            // --- CUSTOM IMAGE LOGO END ---

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Adjust Your Goal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentCyan,
                            focusedLabelColor = accentCyan,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Hours") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentCyan,
                            focusedLabelColor = accentCyan,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            goalsViewModel.updateGoal(goalId, title, duration, userId, navController, context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurpleBtn)
                    ) {
                        Text("SAVE CHANGES", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}