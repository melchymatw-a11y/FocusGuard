package FocusGuard.ui.theme.screens.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import FocusGuard.data.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController) {
    val goalsViewModel: GoalsViewModel = viewModel()

    val allGoals = goalsViewModel.allGoals
    val totalGoalCount = allGoals.size
    val totalHoursPlanned = allGoals.sumOf { it.durationHours.toDoubleOrNull() ?: 0.0 }
    val averageEffort = if (totalGoalCount > 0) totalHoursPlanned / totalGoalCount else 0.0

    // Brand Color Palette
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8
    val accentCyan = Color(0xFF06B6D4) // #06B6D4

    Scaffold(
        containerColor = deepPurpleBg, // Applied Deep Purple Background
        topBar = {
            TopAppBar(
                title = { Text("GOAL ANALYTICS", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Operational Overview",
                    fontSize = 18.sp,
                    color = Color.White, // White text on purple background
                    fontWeight = FontWeight.Bold
                )
            }

            // --- TOTAL ACTIVE GOALS ---
            item {
                RealStatCard(
                    label = "Total Active Goals",
                    value = totalGoalCount.toString(),
                    description = "Live records from Firebase Database",
                    accentColor = accentCyan
                )
            }

            // --- TOTAL PLANNED LABOR ---
            item {
                RealStatCard(
                    label = "Total Planned Labor",
                    value = "${"%.1f".format(totalHoursPlanned)} Hrs",
                    description = "Sum of all duration hours set",
                    accentColor = accentCyan
                )
            }

            // --- AVERAGE GOAL WEIGHT ---
            item {
                RealStatCard(
                    label = "Average Goal Weight",
                    value = "${"%.1f".format(averageEffort)} Hrs",
                    description = "Average time allocated per objective",
                    accentColor = accentCyan
                )
            }
        }
    }
}

@Composable
fun RealStatCard(label: String, value: String, description: String, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // White cards
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = value,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black // Dark text for value
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = accentColor, // Cyan highlight for description/stats
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}