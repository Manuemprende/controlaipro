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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementsScreen(
    viewModel: FinanceViewModel
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val selectedType by viewModel.selectedTypeFilter.collectAsState()

    var activeTransactionForMenu by remember { mutableStateOf<Transaction?>(null) }
    var showEditDialog by remember { mutableStateOf<Transaction?>(null) }

    // Categories
    val categories = listOf("SaaS", "Consultoría", "Software", "Publicidad", "Comida", "VPS", "Suscripciones", "Transporte", "Otros")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("movements_screen_container")
    ) {
        Text(
            text = "Historial de Movimientos",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Buscar transacciones...", color = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("search_text_field"),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
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

        // Quick Filters row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type filters
            val types = listOf("Todos", "Ingreso", "Egreso")
            types.forEach { t ->
                val isSelected = (t == "Todos" && selectedType == null) || (selectedType == t)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PrimaryBlue else CardColor)
                        .clickable {
                            viewModel.selectedTypeFilter.value = if (t == "Todos") null else t
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = t,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Category Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtro:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            // Horizontally Scrollable Category Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset category filter button
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCategory == null) PrimaryBlue.copy(alpha = 0.2f) else CardColor)
                            .clickable { viewModel.selectedCategoryFilter.value = null }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Limpiar Cat.",
                            color = if (selectedCategory == null) PrimaryBlue else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else CardColor)
                            .clickable { viewModel.selectedCategoryFilter.value = cat }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) PrimaryBlue else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Transactions List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Sin movimientos",
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No se encontraron movimientos",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .testTag("transactions_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onClick = { activeTransactionForMenu = transaction }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Action menu dialog
    activeTransactionForMenu?.let { transaction ->
        AlertDialog(
            onDismissRequest = { activeTransactionForMenu = null },
            containerColor = CardColor,
            title = { Text(text = transaction.title, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Monto: $${String.format("%,.2f", transaction.amount)}",
                        color = if (transaction.isIncome) SuccessGreen else ErrorRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text("Selecciona una acción rápida para este movimiento:", color = TextSecondary, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.duplicateTransaction(transaction)
                            activeTransactionForMenu = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ElementColor)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Duplicar", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = {
                            showEditDialog = transaction
                            activeTransactionForMenu = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = {
                            viewModel.deleteTransaction(transaction)
                            activeTransactionForMenu = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        )
    }

    // Edit Dialog
    showEditDialog?.let { transaction ->
        var editTitle by remember { mutableStateOf(transaction.title) }
        var editAmount by remember { mutableStateOf(transaction.amount.toString()) }
        var editCategory by remember { mutableStateOf(transaction.category) }
        var editIsIncome by remember { mutableStateOf(transaction.isIncome) }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            containerColor = CardColor,
            title = { Text("Editar Movimiento", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Concepto", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editAmount,
                        onValueChange = { editAmount = it },
                        label = { Text("Monto", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editIsIncome = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (editIsIncome) SuccessGreen else ElementColor)
                        ) {
                            Text("Ingreso", color = if (editIsIncome) Color.Black else TextPrimary)
                        }

                        Button(
                            onClick = { editIsIncome = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (!editIsIncome) ErrorRed else ElementColor)
                        ) {
                            Text("Egreso", color = if (!editIsIncome) Color.White else TextPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = { editCategory = it },
                        label = { Text("Categoría", color = TextSecondary) },
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
                    TextButton(onClick = { showEditDialog = null }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            val amt = editAmount.toDoubleOrNull() ?: transaction.amount
                            viewModel.updateTransaction(
                                transaction.copy(
                                    title = editTitle,
                                    amount = amt,
                                    category = editCategory,
                                    isIncome = editIsIncome
                                )
                            )
                            showEditDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("transaction_item_${transaction.id}"),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon background with color accent
            val isIncome = transaction.isIncome
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isIncome) SuccessGreen.copy(alpha = 0.12f)
                        else ErrorRed.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        transaction.category.contains("IA", ignoreCase = true) -> Icons.Default.AutoAwesome
                        transaction.category.contains("VPS", ignoreCase = true) || transaction.category.contains("Server", ignoreCase = true) -> Icons.Default.CloudQueue
                        transaction.category.contains("SaaS", ignoreCase = true) || transaction.category.contains("Software", ignoreCase = true) -> Icons.Default.Code
                        transaction.category.contains("Publicidad", ignoreCase = true) || transaction.category.contains("Ads", ignoreCase = true) -> Icons.Default.Campaign
                        transaction.category.contains("Comida", ignoreCase = true) -> Icons.Default.Restaurant
                        transaction.category.contains("Transporte", ignoreCase = true) -> Icons.Default.DirectionsCar
                        isIncome -> Icons.Default.TrendingUp
                        else -> Icons.Default.ReceiptLong
                    },
                    contentDescription = transaction.category,
                    tint = if (isIncome) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (transaction.costCenter != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Pro: ${transaction.costCenter}",
                                color = PrimaryBlue,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"}$${String.format("%,.2f", transaction.amount)}",
                    color = if (isIncome) SuccessGreen else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.formattedDate,
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
