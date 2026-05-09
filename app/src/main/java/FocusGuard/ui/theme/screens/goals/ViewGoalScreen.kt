package FocusGuard.ui.theme.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import FocusGuard.data.GoalsViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewGoalsScreen(navController: NavController) {
    val goalsViewModel: GoalsViewModel = viewModel()
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGoalId by remember { mutableStateOf("") }

    // Consistent Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8
    val accentCyan = Color(0xFF06B6D4) // #06B6D4
    val deleteRed = Color(0xFFEF4444) // #EF4444

    LaunchedEffect(Unit) {
        goalsViewModel.fetchGoals(userId, context)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Goal?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this goal? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    goalsViewModel.deleteGoal(selectedGoalId, userId, context)
                    showDeleteDialog = false
                }) {
                    Text("DELETE", color = deleteRed, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        containerColor = deepPurpleBg, // Applied Deep Purple Background
        topBar = {
            TopAppBar(
                title = { Text("MY FOCUS GOALS", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = deepPurpleBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // White TopBar
                    titleContentColor = deepPurpleBg // Purple text
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (goalsViewModel.allGoals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No goals found. Start focusing!", color = Color.White.copy(alpha = 0.7f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(goalsViewModel.allGoals) { goal ->
                        GoalItem(
                            title = goal.title,
                            hours = goal.durationHours,
                            accentColor = accentCyan,
                            deleteColor = deleteRed,
                            onStartClick = {
                                navController.navigate("tracking/${goal.title}")
                            },
                            onEditClick = {
                                navController.navigate("update_goal/${goal.goalId}")
                            },
                            onDeleteClick = {
                                selectedGoalId = goal.goalId
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalItem(
    title: String,
    hours: String,
    accentColor: Color,
    deleteColor: Color,
    onStartClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // White background for card
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black // Dark text
                )
                Text(
                    text = "Target: $hours Hours",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Row {
                // START SESSION BUTTON (Cyan Accent)
                IconButton(onClick = onStartClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = accentColor
                    )
                }

                // EDIT BUTTON (Gray/Secondary)
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray
                    )
                }

                // DELETE BUTTON (Red #EF4444)
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = deleteColor
                    )
                }
            }
        }
    }
}