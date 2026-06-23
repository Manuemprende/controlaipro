package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            MyApplicationTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    LoginScreen(onLoginSuccess = { isLoggedIn = true })
                } else {
                    var currentTab by remember { mutableIntStateOf(0) }
                    var showAlertsDialog by remember { mutableStateOf(false) }

                    val alerts by viewModel.alerts.collectAsState()
                    val unreadAlertsCount = alerts.count { !it.isRead }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            // Custom styled Premium Bottom Navigation Bar (Stripe/Revolut inspired)
                            NavigationBar(
                                containerColor = CardColor,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    .testTag("bottom_nav_bar")
                            ) {
                                val items = listOf(
                                    Triple(Icons.Default.Home, "Resumen", 0),
                                    Triple(Icons.Default.ReceiptLong, "Movimientos", 1),
                                    Triple(Icons.Default.AddCircle, "Nuevo", 2),
                                    Triple(Icons.Default.CreditCard, "Suscripciones", 3),
                                    Triple(Icons.Default.GridView, "Más", 4)
                                )

                                items.forEach { (icon, label, index) ->
                                    val isSelected = currentTab == index
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = index },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = label,
                                                tint = if (isSelected) PrimaryBlue else TextSecondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                color = if (isSelected) PrimaryBlue else TextSecondary,
                                                fontSize = 9.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = ElementColor
                                        ),
                                        modifier = Modifier.testTag("nav_tab_$index")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BackgroundColor)
                                .padding(bottom = innerPadding.calculateBottomPadding()) // Respect navigation safe zones
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                modifier = Modifier.fillMaxSize(),
                                label = "screen_tab_animation"
                            ) { tab ->
                                when (tab) {
                                    0 -> SummaryScreen(
                                        viewModel = viewModel,
                                        onNavigateToTab = { currentTab = it },
                                        onShowAlertsDialog = { showAlertsDialog = true }
                                    )
                                    1 -> MovementsScreen(viewModel = viewModel)
                                    2 -> AddScreen(
                                        viewModel = viewModel,
                                        onSuccess = { currentTab = 1 } // Redirect to movements history
                                    )
                                    3 -> SubscriptionsScreen(viewModel = viewModel)
                                    4 -> MoreScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Alerts notification dialog
                    if (showAlertsDialog) {
                        AlertDialog(
                            onDismissRequest = { showAlertsDialog = false },
                            containerColor = CardColor,
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Alertas",
                                        tint = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Alertas Inteligentes", color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            },
                            text = {
                                Box(modifier = Modifier.heightIn(max = 300.dp)) {
                                    if (alerts.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No tienes alertas activas.", color = TextSecondary, fontSize = 13.sp)
                                        }
                                    } else {
                                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            items(alerts) { alert ->
                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (alert.isRead) ElementColor.copy(alpha = 0.5f) else ElementColor
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = alert.title,
                                                                color = if (alert.isRead) TextSecondary else TextPrimary,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 12.sp
                                                            )
                                                            Text(text = alert.description, color = TextSecondary, fontSize = 11.sp)
                                                            Text(text = alert.formattedTime, color = TextSecondary.copy(alpha = 0.6f), fontSize = 9.sp)
                                                        }
                                                        if (!alert.isRead) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clip(CircleShape)
                                                                    .background(PrimaryBlue)
                                                                    .clickable { viewModel.markAlertAsRead(alert) },
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = "Read",
                                                                    tint = Color.Black,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showAlertsDialog = false }) {
                                    Text("Cerrar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
