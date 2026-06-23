package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

@Composable
fun SummaryScreen(
    viewModel: FinanceViewModel,
    onNavigateToTab: (Int) -> Unit,
    onShowAlertsDialog: () -> Unit
) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalSavings by viewModel.totalSavings.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val selectedPeriod by viewModel.selectedPeriodFilter.collectAsState()
    val aiInsight by viewModel.aiInsight.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    val unreadAlertsCount = alerts.count { !it.isRead }
    val scrollState = rememberScrollState()
    val SayayinGold = Color(0xFFFFC107)
    var showSayayinRanksDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("summary_screen_container")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Control AI Logo",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "CONTROL AI PRO",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Inteligencia Financiera SaaS",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Alerts bell with unread badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CardColor)
                    .clickable { onShowAlertsDialog() }
                    .testTag("alerts_bell_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alertas",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                if (unreadAlertsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(ErrorRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadAlertsCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Total Balance Display Card (Revolut/Stripe Style) with Sayayin Aura
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .testTag("balance_card")
                .drawWithContent {
                    drawContent()
                    // Sayayin Gold & Cyan Aura Glowing Border
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.6f),
                                SayayinGold.copy(alpha = 0.5f)
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                },
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Balance Total",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$${String.format("%,.2f", totalIncome - totalExpense)}",
                    color = TextPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Income / Expenses row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Ingresos",
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Ingresos", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%,.2f", totalIncome)}",
                                color = SuccessGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ErrorRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = "Egresos",
                                tint = ErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Egresos", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%,.2f", totalExpense)}",
                                color = ErrorRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Calculate and display Sayayin Power Level
        val savingRate = if (totalIncome > 0.0) (totalIncome - totalExpense) / totalIncome else 0.0
        val powerLevel = if (totalIncome == 0.0) {
            0
        } else if (savingRate < 0.0) {
            (100 + (totalIncome * 0.01)).toInt().coerceIn(10, 999)
        } else {
            val base = 1000 + (savingRate * 8000)
            val bonus = ((totalIncome - totalExpense) * 0.25).coerceAtMost(10000.0)
            (base + bonus).toInt()
        }

        val warriorTitle: String
        val warriorDesc: String
        val statusColor: Color
        val showGoldGlow: Boolean

        if (totalIncome == 0.0) {
            warriorTitle = "Guerrero en Entrenamiento"
            warriorDesc = "Registra movimientos para despertar tu Ki."
            statusColor = TextSecondary
            showGoldGlow = false
        } else if (savingRate < 0.0) {
            warriorTitle = "Ki Bajo (Estado Crítico)"
            warriorDesc = "¡Tu energía se drena! Tus egresos superan tus ingresos."
            statusColor = ErrorRed
            showGoldGlow = false
        } else if (savingRate in 0.0..0.15) {
            warriorTitle = "Guerrero Clase Baja"
            warriorDesc = "Ki Base. Estás sobreviviendo en el campo de batalla financiero."
            statusColor = TextSecondary
            showGoldGlow = false
        } else if (savingRate in 0.15..0.35) {
            warriorTitle = "Guerrero Clase Media"
            warriorDesc = "¡Poder ascendente! Acercándote al Súper Sayayin."
            statusColor = PrimaryBlue
            showGoldGlow = false
        } else if (savingRate in 0.35..0.55) {
            warriorTitle = "¡Súper Sayayin!"
            warriorDesc = "¡Poder asombroso! Has roto las barreras ordinarias de ahorro."
            statusColor = SayayinGold
            showGoldGlow = true
        } else if (savingRate in 0.55..0.75) {
            warriorTitle = "Súper Sayayin Fase 3"
            warriorDesc = "¡Energía total! Tu Ki financiero estremece el mercado."
            statusColor = SayayinGold
            showGoldGlow = true
        } else {
            warriorTitle = "Ultra Instinto Financiero"
            warriorDesc = "¡ES DE MÁS DE 9,000! Eres el Dios supremo del ahorro."
            statusColor = Color(0xFF00E5FF)
            showGoldGlow = true
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clickable { showSayayinRanksDialog = true }
                .drawWithContent {
                    drawContent()
                    if (showGoldGlow) {
                        // Sayayin energetic border glow
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    statusColor.copy(alpha = 0.6f),
                                    Color(0xFFFFC107).copy(alpha = 0.5f)
                                )
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
                .testTag("sayayin_power_card"),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Sayayin Power",
                                tint = statusColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Nivel de Poder Guerrero",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = warriorTitle,
                                color = statusColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Power level in Ki units
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Ki Financiero",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (powerLevel >= 9000) "¡OVER 9000! (${String.format("%,d", powerLevel)})" else "${String.format("%,d", powerLevel)} Ki",
                            color = if (powerLevel >= 9000) SayayinGold else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar (Ki gauge)
                val targetMaxKi = 12000f
                val progressFraction = (powerLevel.toFloat() / targetMaxKi).coerceIn(0.05f, 1f)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElementColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressFraction)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        statusColor,
                                        if (showGoldGlow) Color(0xFFFFC107) else statusColor
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = warriorDesc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Ver todos los rangos Sayayin ➔",
                    color = if (showGoldGlow) SayayinGold else PrimaryBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        // Period filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val periods = listOf("Hoy", "7 días", "30 días", "90 días", "Todos")
            periods.forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryBlue else ElementColor)
                        .clickable { viewModel.selectedPeriodFilter.value = period }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // TradingView/Stripe Interactive Style Cash Flow Chart
        Text(
            text = "Evolución de Flujo Digital",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 24.dp)
                .testTag("chart_card"),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (transactions.isEmpty()) {
                    Text(
                        text = "No hay datos suficientes para graficar",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                } else {
                    // Custom painted Stripe-like spline chart with gradients
                    val sortedTrans = remember(transactions) {
                        transactions.sortedBy { it.date }.takeLast(8)
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Draw Grid lines
                        val gridCount = 4
                        for (i in 0..gridCount) {
                            val y = (height / gridCount) * i
                            drawLine(
                                color = ElementColor.copy(alpha = 0.3f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (sortedTrans.size > 1) {
                            val maxAmount = sortedTrans.maxOfOrNull { it.amount } ?: 100.0
                            val points = sortedTrans.mapIndexed { index, t ->
                                val x = (width / (sortedTrans.size - 1)) * index
                                // Scale y (higher values are represented higher up, i.e., smaller y-coord)
                                val ratio = if (maxAmount > 0) (t.amount / maxAmount).toFloat() else 0.5f
                                val y = height - (ratio * height * 0.75f) - (height * 0.1f)
                                Offset(x, y)
                            }

                            // Create path for area fill
                            val fillPath = Path().apply {
                                moveTo(0f, height)
                                points.forEachIndexed { i, pt ->
                                    if (i == 0) lineTo(pt.x, pt.y)
                                    else lineTo(pt.x, pt.y)
                                }
                                lineTo(width, height)
                                close()
                            }

                            // Draw Area Gradient
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )

                            // Create spline stroke path
                            val strokePath = Path().apply {
                                points.forEachIndexed { i, pt ->
                                    if (i == 0) moveTo(pt.x, pt.y)
                                    else lineTo(pt.x, pt.y)
                                }
                            }

                            // Draw Spline Line
                            drawPath(
                                path = strokePath,
                                color = PrimaryBlue,
                                style = Stroke(width = 2.5.dp.toPx())
                            )

                            // Draw Dots on points
                            points.forEachIndexed { idx, pt ->
                                val isIncome = sortedTrans[idx].isIncome
                                drawCircle(
                                    color = if (isIncome) SuccessGreen else ErrorRed,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick shortcut to make user feels premium
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clickable { onNavigateToTab(1) }, // Navigate to Movements
            colors = CardDefaults.cardColors(containerColor = ElementColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ver Todos los Movimientos",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Edita, elimina y filtra tus transacciones",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "→",
                    color = PrimaryBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Glowing AI Summary Insights Card (Linear/Notion inspired) -> MOVED TO THE END OF SUMMARY SCREEN
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .drawWithContent {
                    drawContent()
                    // Custom aesthetic border glow with gold-purple Sayayin aura gradient
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.5f),
                                SayayinGold.copy(alpha = 0.4f),
                                TertiaryDark.copy(alpha = 0.5f)
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                }
                .testTag("ai_insights_card"),
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = SayayinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Resumen IA Financiero",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                AnimatedContent(
                    targetState = aiInsight,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ai_insight_transition"
                ) { text ->
                    Text(
                        text = text,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Sayayin Ranks detail dialog
        if (showSayayinRanksDialog) {
            AlertDialog(
                onDismissRequest = { showSayayinRanksDialog = false },
                containerColor = CardColor,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Poder Financiero",
                            tint = SayayinGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escalafón de Poder Financiero", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    val lazyListState = rememberLazyListState()
                    val canScrollForward by remember { derivedStateOf { lazyListState.canScrollForward } }
                    val canScrollBackward by remember { derivedStateOf { lazyListState.canScrollBackward } }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .heightIn(max = 260.dp)
                                .fadingEdge(
                                    showTop = canScrollBackward,
                                    showBottom = canScrollForward
                                ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Tu rango de Guerrero Sayayin se calcula según tu Tasa de Ahorro mensual (ingresos que conservas libres de gastos):",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            val currentRankTitle = when {
                                totalIncome == 0.0 -> "Clase Baja (Ki Base)"
                                savingRate < 0.0 -> "Ki Bajo (Bajo Cero)"
                                savingRate <= 0.15 -> "Clase Baja (Ki Base)"
                                savingRate <= 0.35 -> "Clase Media (Ki Ascendente)"
                                savingRate <= 0.55 -> "Súper Sayayin"
                                savingRate <= 0.75 -> "Súper Sayayin Fase 3"
                                else -> "Ultra Instinto"
                            }

                            val ranksList = listOf(
                                Triple("Ki Bajo (Bajo Cero)", "Gastos superan tus ingresos (Déficit). ¡Tu energía se drena rápidamente! Reduce gastos hoy.", ErrorRed),
                                Triple("Clase Baja (Ki Base)", "Ahorras menos del 15%. Sobrevives en batalla pero necesitas acumular más poder.", TextSecondary),
                                Triple("Clase Media (Ki Ascendente)", "Ahorras de 15% a 35%. Excelente control de Ki, estás cerca de romper tus límites.", PrimaryBlue),
                                Triple("Súper Sayayin", "Ahorras de 35% a 55%. Has roto la barrera ordinaria de ahorro y tu poder resplandece.", SayayinGold),
                                Triple("Súper Sayayin Fase 3", "Ahorras de 55% a 75%. ¡Energía masiva desbordante que domina el mercado financiero!", SayayinGold),
                                Triple("Ultra Instinto", "Ahorras más de 75%. ¡NIVEL DE PODER MÁS DE 9,000! Has alcanzado la iluminación financiera.", Color(0xFF00E5FF))
                            )

                            items(ranksList) { (title, desc, color) ->
                                val isCurrentRank = title == currentRankTitle
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrentRank) color.copy(alpha = 0.12f) else ElementColor.copy(alpha = 0.6f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = if (isCurrentRank) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(color)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = title,
                                                    color = color,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            if (isCurrentRank) {
                                                Text(
                                                    text = "TU RANGO",
                                                    color = Color.Black,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(color)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = desc,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Floating down indicator when scroll is available
                        if (canScrollForward) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, CardColor.copy(alpha = 0.95f), CardColor),
                                            startY = 0f,
                                            endY = 50f
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Deslizar para ver más",
                                        tint = SayayinGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Desliza para ver más",
                                        color = SayayinGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSayayinRanksDialog = false }) {
                        Text("¡Entendido, Insecto!", color = SayayinGold, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

fun Modifier.fadingEdge(
    showTop: Boolean,
    showBottom: Boolean,
    topHeight: Float = 40f,
    bottomHeight: Float = 40f
): Modifier = this
    .graphicsLayer { alpha = 0.99f } // Required for BlendMode.DstIn to work
    .drawWithContent {
        drawContent()
        if (showTop) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topHeight
                ),
                blendMode = androidx.compose.ui.graphics.BlendMode.DstIn
            )
        }
        if (showBottom) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - bottomHeight,
                    endY = size.height
                ),
                blendMode = androidx.compose.ui.graphics.BlendMode.DstIn
            )
        }
    }
