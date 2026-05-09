package FocusGuard.ui.theme.screens.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import FocusGuard.data.GoalsViewModel
import FocusGuard.navigation.ROUTE_ADD_GOAL
import FocusGuard.navigation.ROUTE_VIEW_GOALS
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    val context = LocalContext.current
    val goalsViewModel: GoalsViewModel = viewModel()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8
    val primaryPurpleBtn = Color(0xFF7C3AED) // #7C3AED
    val accentCyan = Color(0xFF06B6D4) // #06B6D4

    Scaffold(
        containerColor = deepPurpleBg, // Applied Deep Purple Background
        topBar = {
            TopAppBar(
                title = { Text("SET NEW FOCUS GOAL", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = deepPurpleBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // White TopBar
                    titleContentColor = deepPurpleBg // Purple text on top bar
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Text on purple background is White
            Text(
                text = "What are we focusing on?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // White Card containing inputs
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
                        label = { Text("Goal Title (e.g. Studying Kotlin)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentCyan,
                            focusedLabelColor = accentCyan,
                            unfocusedBorderColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = duration,
                        onValueChange = {
                            if (it.isEmpty() || it.toDoubleOrNull() != null || it.endsWith(".")) {
                                duration = it
                            }
                        },
                        label = { Text("Target Hours (e.g. 0.5 for 30 mins)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentCyan,
                            focusedLabelColor = accentCyan,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Text(
                        text = "Use decimals: 0.25 = 15m, 0.5 = 30m",
                        fontSize = 11.sp,
                        color = accentCyan, // Cyan highlight for the tip
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, top = 8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Primary Purple Button
                    Button(
                        onClick = {
                            goalsViewModel.saveGoal(title, duration, userId, navController, context)
                            navController.navigate(ROUTE_VIEW_GOALS) {
                                popUpTo(ROUTE_ADD_GOAL) { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryPurpleBtn)
                    ) {
                        Text("Add to My Goals", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}