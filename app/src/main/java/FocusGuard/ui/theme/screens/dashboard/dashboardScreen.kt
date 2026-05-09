package FocusGuard.ui.theme.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import FocusGuard.data.AuthViewModel
import FocusGuard.data.GoalsViewModel
import FocusGuard.models.UserModel
import FocusGuard.navigation.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val goalsViewModel: GoalsViewModel = viewModel()
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userData by remember { mutableStateOf<UserModel?>(null) }

    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8)  // Exact hex requested
    val indigoText = Color(0xFF3730A3)    // Professional Indigo
    val blueHero = Color(0xFF2563EB)      // Blue for gradient
    val cyanHero = Color(0xFF06B6D4)      // Cyan for gradient
    val lightPurpleCard = Color(0xFF7E22CE) // Slightly lighter for Partner card

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            authViewModel.fetchUserData(uid) { user -> userData = user }
            goalsViewModel.fetchGoals(uid, context)
        }
    }

    Scaffold(
        containerColor = deepPurpleBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FOCUS GUARD", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = deepPurpleBg
                ),
                actions = {
                    IconButton(onClick = { authViewModel.logout(navController, context) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = deepPurpleBg)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_ADD_GOAL) },
                containerColor = deepPurpleBg,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header: Welcome Section
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Welcome, ${userData?.name ?: "User"}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                val totalHours = goalsViewModel.allGoals.sumOf { it.durationHours.toDoubleOrNull() ?: 0.0 }
                Text(
                    text = "Total Project Labor: ${"%.1f".format(totalHours)} Hours Across ${goalsViewModel.allGoals.size} Tasks",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Hero Card: System Analysis (Blue to Cyan Gradient)
            item {
                Card(
                    onClick = { navController.navigate(ROUTE_ANALYTICS) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Brush.horizontalGradient(listOf(blueHero, cyanHero)))
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SYSTEM ANALYSIS", color = Color.White.copy(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("View Reports", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Track labor hours & productivity", color = Color.White.copy(0.9f), fontSize = 13.sp)
                            }
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                }
            }

            // Accountability Partners Card (Lighter Purple)
            item {
                Card(
                    onClick = { navController.navigate(ROUTE_PARTNER) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = lightPurpleCard)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Accountability Partners", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Share progress with your team", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Quick Stats Row (White Cards)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        onClick = { navController.navigate(ROUTE_VIEW_GOALS) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Active Goals", color = Color.Gray, fontSize = 12.sp)
                            Text("${goalsViewModel.allGoals.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        onClick = { navController.navigate(ROUTE_ADD_GOAL) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Quick Action", color = Color.Gray, fontSize = 12.sp)
                            Text("Add New", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = deepPurpleBg)
                        }
                    }
                }
            }

            item {
                Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }

            // Activity Items (White Cards with Indigo Text)
            items(goalsViewModel.allGoals.take(3)) { goal ->
                val rawDuration = goal.durationHours.toDoubleOrNull() ?: 0.0
                val formattedDuration = if (rawDuration % 1.0 == 0.0) rawDuration.toInt().toString() else "%.1f".format(rawDuration)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = cyanHero)
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(goal.title, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("$formattedDuration Hours Allocated", fontSize = 12.sp, color = indigoText)
                        }
                        IconButton(onClick = { navController.navigate("tracking/${goal.title}") }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = deepPurpleBg)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}