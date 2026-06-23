package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Goal
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    viewModel: FinanceViewModel
) {
    var selectedMoreTab by remember { mutableStateOf(0) } // 0: Analítica IA, 1: Centro de Costos, 2: Objetivos

    val transactions by viewModel.transactions.collectAsState()
    val goals by viewModel.goals.collectAsState()

    val scrollState = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("more_screen_container")
    ) {
        Text(
            text = "Herramientas Inteligentes",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Navigation Tabs for More (IA Chat, Cost Center, Goals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardColor)
                .padding(4.dp)
                .padding(bottom = 16.dp)
        ) {
            val tabs = listOf("Analítica IA", "Centro Costos", "Metas")
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedMoreTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ElementColor else Color.Transparent)
                        .clickable { selectedMoreTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) PrimaryBlue else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content switching
        when (selectedMoreTab) {
            0 -> {
                // IA Chatbot Panel
                val chatMessages by viewModel.chatMessages.collectAsState()
                val isChatLoading by viewModel.isChatLoading.collectAsState()
                var currentMessageText by remember { mutableStateOf("") }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pregunta a tu Consultor IA",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Conversational messages box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardColor)
                            .padding(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            reverseLayout = true // Standard chat layout
                        ) {
                            if (isChatLoading) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "La IA está procesando...", color = PrimaryBlue, fontSize = 11.sp)
                                    }
                                }
                            }

                            items(chatMessages.asReversed()) { (text, isUser) ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp,
                                                    bottomStart = if (isUser) 12.dp else 2.dp,
                                                    bottomEnd = if (isUser) 2.dp else 12.dp
                                                )
                                            )
                                            .background(if (isUser) PrimaryBlue else ElementColor)
                                            .padding(12.dp)
                                            .widthIn(max = 260.dp)
                                    ) {
                                        Text(
                                            text = text,
                                            color = if (isUser) Color.Black else TextPrimary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick suggestion chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val suggestions = listOf(
                            "¿Dónde gasto más dinero?",
                            "¿Cuál es mi ahorro?",
                            "Dame recomendaciones de IA",
                            "Predicciones de finanzas"
                        )
                        items(suggestions) { question ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(ElementColor)
                                    .clickable { viewModel.sendChatMessage(question) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(text = question, color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Input bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = currentMessageText,
                            onValueChange = { currentMessageText = it },
                            placeholder = { Text("Escribe una consulta a la IA...", color = TextSecondary, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = ElementColor,
                                focusedContainerColor = CardColor,
                                unfocusedContainerColor = CardColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                                .clickable {
                                    if (currentMessageText.isNotBlank()) {
                                        viewModel.sendChatMessage(currentMessageText)
                                        currentMessageText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            1 -> {
                // Cost Center (Modo Pro)
                val costCenterExpenses = remember(transactions) {
                    val map = mutableMapOf<String, Double>()
                    transactions.filter { !it.isIncome && it.costCenter != null }.forEach {
                        map[it.costCenter!!] = (map[it.costCenter] ?: 0.0) + it.amount
                    }
                    map.toList().sortedByDescending { it.second }
                }

                val totalProExpense = costCenterExpenses.sumOf { it.second }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Centro de Costos Operativos",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Modo Pro", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "Costos fijos asociados a infraestructura, software, APIs de Inteligencia Artificial y publicidad digital.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Totals display
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.TrendingDown, contentDescription = "Total Pro", tint = ErrorRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Costo Operativo Total", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    text = "$${String.format("%,.2f", totalProExpense)}",
                                    color = TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Grid / List of sectors
                    if (costCenterExpenses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Registra egresos con un Centro de Costos pro para ver el desglose operativo.", color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(costCenterExpenses) { (category, amount) ->
                                val ratio = if (totalProExpense > 0) (amount / totalProExpense).toFloat() else 0f
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = category, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "$${String.format("%,.2f", amount)} (${String.format("%.1f", ratio * 100)}%)",
                                            color = PrimaryBlue,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = ratio,
                                        color = PrimaryBlue,
                                        trackColor = ElementColor,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            2 -> {
                // Goals (Metas)
                var showAddGoalDialog by remember { mutableStateOf(false) }
                var selectedGoalForFunds by remember { mutableStateOf<Goal?>(null) }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Metas de Ahorro",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "+ Nueva Meta",
                            color = PrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showAddGoalDialog = true }
                        )
                    }

                    if (goals.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Crea una meta para visualizar el progreso de tus objetivos.", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(goals) { goal ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedGoalForFunds = goal },
                                    colors = CardDefaults.cardColors(containerColor = CardColor),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = goal.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "$${String.format("%,.0f", goal.currentAmount)} / $${String.format("%,.0f", goal.targetAmount)}",
                                                color = SuccessGreen,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Goal Progress Bar
                                        LinearProgressIndicator(
                                            progress = goal.progress,
                                            color = SuccessGreen,
                                            trackColor = ElementColor,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Progreso: ${String.format("%.1f", goal.progress * 100)}%",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )

                                            Text(
                                                text = "+ Ahorrar Fondos",
                                                color = PrimaryBlue,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

                // Add goal dialog
                if (showAddGoalDialog) {
                    var goalName by remember { mutableStateOf("") }
                    var goalTarget by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showAddGoalDialog = false },
                        containerColor = CardColor,
                        title = { Text("Nueva Meta de Ahorro", color = TextPrimary, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = goalName,
                                    onValueChange = { goalName = it },
                                    label = { Text("¿Qué deseas comprar?", color = TextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = ElementColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = goalTarget,
                                    onValueChange = { goalTarget = it },
                                    label = { Text("Meta de ahorro ($)", color = TextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = ElementColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { showAddGoalDialog = false }) {
                                    Text("Cancelar", color = TextSecondary)
                                }
                                Button(
                                    onClick = {
                                        val tgt = goalTarget.toDoubleOrNull() ?: 0.0
                                        if (goalName.isNotBlank() && tgt > 0) {
                                            viewModel.addGoal(goalName, tgt)
                                            showAddGoalDialog = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black)
                                ) {
                                    Text("Crear")
                                }
                            }
                        }
                    )
                }

                // Add funds dialog
                selectedGoalForFunds?.let { goal ->
                    var fundAmt by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { selectedGoalForFunds = null },
                        containerColor = CardColor,
                        title = { Text("Añadir Fondos: ${goal.name}", color = TextPrimary, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Ingresa el monto que deseas sumar a esta meta:", color = TextSecondary, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = fundAmt,
                                    onValueChange = { fundAmt = it },
                                    label = { Text("Monto ($)", color = TextSecondary) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = PrimaryBlue,
                                        unfocusedBorderColor = ElementColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.deleteGoal(goal)
                                        selectedGoalForFunds = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Borrar Meta")
                                }

                                Button(
                                    onClick = {
                                        val added = fundAmt.toDoubleOrNull() ?: 0.0
                                        if (added > 0) {
                                            viewModel.addProgressToGoal(goal, added)
                                            selectedGoalForFunds = null
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black),
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text("Añadir")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
