package com.example.ui.screens

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
import com.example.data.Subscription
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: FinanceViewModel
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val monthlyTotal by viewModel.totalMonthlySubscriptionPrice.collectAsState()
    val annualTotal by viewModel.totalAnnualSubscriptionPrice.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Subscription?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("subscriptions_screen_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Control de Suscripciones",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Add button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
                    .clickable { showAddDialog = true }
                    .testTag("add_sub_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Suscripción",
                    tint = Color.Black
                )
            }
        }

        // Totals Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Costo Mensual Total", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%,.2f", monthlyTotal)}",
                        color = PrimaryBlue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Costo Anual Proyectado", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%,.2f", annualTotal)}",
                        color = SuccessGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Notification Status & Test Banner
        val context = androidx.compose.ui.platform.LocalContext.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ElementColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Notificaciones Activas",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Recordatorios de Cancelación",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Te avisamos 5 días antes de vencer para que decidas si cancelar.",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.triggerDemoNotification(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.15f), contentColor = PrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Probar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List Header
        Text(
            text = "Tus Servicios Activos",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Lazy list of subscriptions
        if (subscriptions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No has agregado suscripciones aún.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subscriptions) { sub ->
                    SubscriptionItem(
                        subscription = sub,
                        onClick = { showEditDialog = sub }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var isAnnual by remember { mutableStateOf(false) }
        var nextBillingDate by remember { mutableStateOf("27/06/2026") }
        var category by remember { mutableStateOf("Herramientas IA") }
        var notifyEnabled by remember { mutableStateOf(true) }
        var notifyDaysBefore by remember { mutableStateOf(5) }
        var showDatePicker by remember { mutableStateOf(false) }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                                    timeInMillis = millis
                                }
                                val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).apply {
                                    timeZone = java.util.TimeZone.getDefault()
                                }
                                nextBillingDate = format.format(utcCalendar.time)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Confirmar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = CardColor,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary
                )
            ) {
                DatePicker(state = datePickerState)
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CardColor,
            title = { Text("Nueva Suscripción", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del servicio", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio ($)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Cobro Anual", color = TextSecondary, modifier = Modifier.weight(1f))
                        Switch(
                            checked = isAnnual,
                            onCheckedChange = { isAnnual = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                        )
                    }

                    OutlinedTextField(
                        value = nextBillingDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Próximo cobro (Seleccionar)", color = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = PrimaryBlue)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría (IA, VPS, Hosting, etc.)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = ElementColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Text("Recordatorio de Cancelación", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Activar alerta nativa", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = notifyEnabled,
                            onCheckedChange = { notifyEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                        )
                    }

                    if (notifyEnabled) {
                        Column {
                            Text("Días de anticipación para avisar:", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(bottom = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 3, 5).forEach { days ->
                                    val isSelected = notifyDaysBefore == days
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { notifyDaysBefore = days },
                                        label = { Text("$days ${if (days == 1) "día" else "días"}") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                                            selectedLabelColor = PrimaryBlue,
                                            containerColor = ElementColor,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                    Button(
                        onClick = {
                            val amt = price.toDoubleOrNull() ?: 0.0
                            if (name.isNotBlank() && amt > 0) {
                                viewModel.addSubscription(
                                    name = name,
                                    price = amt,
                                    isAnnual = isAnnual,
                                    nextBillingDate = nextBillingDate,
                                    category = category,
                                    notifyDaysBefore = notifyDaysBefore,
                                    notifyEnabled = notifyEnabled
                                )
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black)
                    ) {
                        Text("Agregar")
                    }
                }
            }
        )
    }

    // Edit/Actions dialog for subscriptions
    showEditDialog?.let { sub ->
        var nextDate by remember { mutableStateOf(sub.nextBillingDate) }
        var priceStr by remember { mutableStateOf(sub.price.toString()) }
        var isPaused = sub.status == "Pausado"
        var notifyEnabled by remember { mutableStateOf(sub.notifyEnabled) }
        var notifyDaysBefore by remember { mutableStateOf(sub.notifyDaysBefore) }
        var showDatePicker by remember { mutableStateOf(false) }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val utcCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                                    timeInMillis = millis
                                }
                                val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).apply {
                                    timeZone = java.util.TimeZone.getDefault()
                                }
                                nextDate = format.format(utcCalendar.time)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Confirmar", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = CardColor,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary
                )
            ) {
                DatePicker(state = datePickerState)
            }
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            containerColor = CardColor,
            title = { Text(sub.name, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Configura o pausa este servicio:", color = TextSecondary, fontSize = 12.sp)

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Valor Mensual/Anual ($)", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nextDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Próximo Cobro", color = TextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = PrimaryBlue)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ElementColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Pausar Suscripción", color = TextSecondary, modifier = Modifier.weight(1f))
                        Switch(
                            checked = isPaused,
                            onCheckedChange = { isPaused = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                        )
                    }

                    HorizontalDivider(color = ElementColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Text("Ajustes de Notificación", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Recibir alerta push nativa", color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = notifyEnabled,
                            onCheckedChange = { notifyEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                        )
                    }

                    if (notifyEnabled) {
                        Column {
                            Text("Días de anticipación para avisar:", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.padding(bottom = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(1, 3, 5).forEach { days ->
                                    val isSelected = notifyDaysBefore == days
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { notifyDaysBefore = days },
                                        label = { Text("$days ${if (days == 1) "día" else "días"}") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                                            selectedLabelColor = PrimaryBlue,
                                            containerColor = ElementColor,
                                            labelColor = TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.deleteSubscription(sub)
                            showEditDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Eliminar")
                    }

                    Button(
                        onClick = {
                            val pr = priceStr.toDoubleOrNull() ?: sub.price
                            viewModel.updateSubscription(
                                sub.copy(
                                    price = pr,
                                    nextBillingDate = nextDate,
                                    status = if (isPaused) "Pausado" else "Activo",
                                    notifyEnabled = notifyEnabled,
                                    notifyDaysBefore = notifyDaysBefore
                                )
                            )
                            showEditDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        )
    }
}

@Composable
fun SubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val isActive = subscription.status == "Activo"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sub_item_${subscription.id}"),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Service brand circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) PrimaryBlue.copy(alpha = 0.12f)
                        else TextSecondary.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        subscription.name.contains("Netflix", ignoreCase = true) -> Icons.Default.Tv
                        subscription.name.contains("ChatGPT", ignoreCase = true) || subscription.name.contains("Claude", ignoreCase = true) -> Icons.Default.AutoAwesome
                        subscription.name.contains("Google", ignoreCase = true) -> Icons.Default.CloudQueue
                        subscription.name.contains("AWS", ignoreCase = true) || subscription.name.contains("DigitalOcean", ignoreCase = true) -> Icons.Default.Dns
                        else -> Icons.Default.CreditCard
                    },
                    contentDescription = subscription.name,
                    tint = if (isActive) PrimaryBlue else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subscription.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isActive) SuccessGreen else ErrorRed)
                    )
                }
                
                val (daysText, daysColor) = if (isActive) {
                    getRemainingDaysText(subscription.nextBillingDate)
                } else {
                    "Pausado" to TextSecondary
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Vence: ${subscription.nextBillingDate}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• $daysText",
                        color = daysColor,
                        fontSize = 11.sp,
                        fontWeight = if (daysColor != TextSecondary) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%,.2f", subscription.price)}",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (subscription.isAnnual) "Anual" else "/mes",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

fun getRemainingDaysText(nextBillingDate: String): Pair<String, Color> {
    try {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val billingDate = sdf.parse(nextBillingDate) ?: return "Cobro: $nextBillingDate" to TextSecondary
        val today = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time

        val diffMs = billingDate.time - today.time
        val diffDays = diffMs / (1000 * 60 * 60 * 24)

        return when {
            diffDays < 0 -> "Expiró/Renovado" to TextSecondary
            diffDays == 0L -> "Se cobra hoy ⚠️" to ErrorRed
            diffDays == 1L -> "Cobra mañana ⚠️" to ErrorRed
            diffDays <= 5L -> "¡Vence en $diffDays días! ⚠️" to Color(0xFFFFC107) // SayayinGold / Orange
            else -> "Faltan $diffDays días" to TextSecondary
        }
    } catch (e: Exception) {
        return "Cobro: $nextBillingDate" to TextSecondary
    }
}
