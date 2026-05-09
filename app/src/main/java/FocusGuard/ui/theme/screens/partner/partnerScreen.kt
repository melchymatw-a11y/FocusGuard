package com.example.focusguard.ui.theme.screens.partner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.focusguard.models.Partner
import com.example.focusguard.models.PartnerProgress
import com.example.focusguard.data.PartnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerScreen(
    navController: NavController,
    viewModel: PartnerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var emailInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Consistent Brand Colors
    val deepPurpleBg = Color(0xFF6B21A8) // #6B21A8
    val primaryPurpleBtn = Color(0xFF7C3AED) // #7C3AED
    val accentCyan = Color(0xFF06B6D4) // #06B6D4
    val deleteRed = Color(0xFFEF4444) // #EF4444

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    Scaffold(
        containerColor = deepPurpleBg, // Updated to brand Deep Purple
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PARTNERS", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White, // White TopBar
                    titleContentColor = deepPurpleBg // Purple Text
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = deepPurpleBg)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Add Partner Card (White background, Dark text)
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add a Partner", fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Enter their email to send a request", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("partner@email.com") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = accentCyan) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentCyan,
                                focusedLabelColor = accentCyan
                            )
                        )
                        Button(
                            onClick = { viewModel.sendPartnerRequest(emailInput.trim()); emailInput = "" },
                            enabled = emailInput.isNotBlank() && !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryPurpleBtn)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Icon(Icons.Default.Send, null)
                            }
                        }
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = accentCyan // Cyan indicator
                    )
                }
            ) {
                val tabs = listOf("Partners", "Requests", "Sent")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, color = if(selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)) }
                    )
                }
            }

            when (selectedTab) {
                0 -> PartnersTab(uiState.connectedPartners, uiState.partnerProgressMap, accentCyan, deleteRed) { viewModel.removePartner(it) }
                1 -> RequestsTab(uiState.receivedRequests, primaryPurpleBtn, { viewModel.acceptRequest(it) }) { viewModel.declineRequest(it) }
                2 -> SentTab(uiState.sentRequests)
            }
        }
    }
}

@Composable
private fun PartnersTab(
    partners: List<Partner>,
    progressMap: Map<String, PartnerProgress>,
    accentColor: Color,
    deleteColor: Color,
    onRemove: (Partner) -> Unit
) {
    if (partners.isEmpty()) {
        EmptyState(Icons.Default.Person, "No partners yet", "Add a partner above to start collaborating")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(partners, key = { it.uid }) { partner ->
            PartnerCard(partner, progressMap[partner.uid], accentColor, deleteColor, onRemove)
        }
    }
}

@Composable
private fun PartnerCard(partner: Partner, progress: PartnerProgress?, accentColor: Color, deleteColor: Color, onRemove: (Partner) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Remove Partner", fontWeight = FontWeight.Bold) },
            text = { Text("Stop tracking focus with ${partner.email}?") },
            confirmButton = { TextButton(onClick = { onRemove(partner); showDialog = false }) { Text("REMOVE", color = deleteColor, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("CANCEL", color = Color.Gray) } }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(partner.email.first().uppercase(), fontWeight = FontWeight.Bold, color = accentColor)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(partner.displayName.ifBlank { partner.email }, fontWeight = FontWeight.Bold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(partner.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Delete, null, tint = deleteColor)
                }
            }

            if (progress != null) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    val hours = progress.totalFocusMinutes / 60.0
                    val formattedHours = "%.1f".format(hours)

                    StatItem("Goals", "${progress.totalGoals}", Icons.Default.CheckCircle, accentColor)
                    StatItem("Labor", "$formattedHours hrs", Icons.Default.Info, accentColor)
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun RequestsTab(requests: List<Partner>, primaryBtnColor: Color, onAccept: (Partner) -> Unit, onDecline: (Partner) -> Unit) {
    if (requests.isEmpty()) {
        EmptyState(Icons.Default.Email, "No pending requests", "Requests will appear here")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(requests, key = { it.uid }) { request ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = primaryBtnColor)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(request.email, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Sent you a partner request", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onDecline(request) }, modifier = Modifier.weight(1f)) { Text("Decline") }
                    Button(onClick = { onAccept(request) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = primaryBtnColor)) { Text("Accept") }
                }
            }
        }
    }
}

@Composable
private fun SentTab(requests: List<Partner>) {
    if (requests.isEmpty()) {
        EmptyState(Icons.Default.Send, "No sent requests", "Requests you send will appear here")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(requests, key = { it.uid }) { request ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, null, tint = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(request.email, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Awaiting response...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, modifier = Modifier.size(64.dp), tint = Color.White.copy(alpha = 0.4f))
        Spacer(Modifier.height(16.dp))
        Text(message, fontWeight = FontWeight.SemiBold, color = Color.White)
        Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
    }
}