package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.theme.*
import com.example.viewmodel.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    viewModel: FinanceViewModel,
    onSuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(0) } // 0: Manual, 1: Scan, 2: Voice

    val isProcessing by viewModel.isProcessingAiInput.collectAsState()
    val aiResult by viewModel.aiResultPreview.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("add_screen_container")
    ) {
        Text(
            text = "Registrar Movimiento",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Method Selector Tab (Manual, Scan, Voice)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardColor)
                .padding(4.dp)
                .padding(bottom = 20.dp)
        ) {
            val tabs = listOf(
                Icons.Default.Edit to "Manual",
                Icons.Default.QrCodeScanner to "Escanear",
                Icons.Default.Mic to "Dictar"
            )
            tabs.forEachIndexed { index, (icon, label) ->
                val isSelected = selectedMethod == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ElementColor else Color.Transparent)
                        .clickable { selectedMethod = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) PrimaryBlue else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // AI processing loading state or result state
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "La Inteligencia Artificial está analizando tu entrada...",
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (aiResult != null) {
            // Display Glow Preview Card of AI results (User can confirm or discard)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBlue, RoundedCornerShape(18.dp))
                    .padding(bottom = 24.dp)
                    .testTag("ai_preview_card"),
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Preview",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resultado de Extracción IA",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Concepto:", color = TextSecondary, fontSize = 11.sp)
                    Text(text = aiResult!!.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Monto:", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%,.2f", aiResult!!.amount)}",
                                color = if (aiResult!!.isIncome) SuccessGreen else TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Categoría:", color = TextSecondary, fontSize = 11.sp)
                            Text(text = aiResult!!.category, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (aiResult!!.costCenter != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Centro de Costos (Modo Pro):", color = TextSecondary, fontSize = 11.sp)
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = aiResult!!.costCenter!!, color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.cancelAiResult() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElementColor)
                        ) {
                            Text("Descartar")
                        }

                        Button(
                            onClick = {
                                viewModel.confirmAiResult()
                                onSuccess()
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.Black)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirmar y Guardar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Based on selected Method Tab
            when (selectedMethod) {
                0 -> {
                    // Manual Registration Form
                    var title by remember { mutableStateOf("") }
                    var amount by remember { mutableStateOf("") }
                    var isIncome by remember { mutableStateOf(false) }
                    var category by remember { mutableStateOf("SaaS") }
                    var selectedCostCenter by remember { mutableStateOf<String?>(null) }
                    var paymentMethod by remember { mutableStateOf("Tarjeta") }

                    val categories = listOf("SaaS", "Consultoría", "Software", "Publicidad", "Comida", "VPS", "Suscripciones", "Transporte", "Otros")
                    val costCenters = listOf("IA", "VPS", "Hosting", "Dominios", "Publicidad Meta", "Google Ads", "Herramientas SaaS", "Automatizaciones", "Software")

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Ej. Licencia ChatGPT, Venta SaaS", color = TextSecondary) },
                            label = { Text("Concepto o Empresa", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = ElementColor,
                                focusedContainerColor = CardColor,
                                unfocusedContainerColor = CardColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("add_title_input")
                        )

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = { Text("Ej. 20.00, 15000", color = TextSecondary) },
                            label = { Text("Monto ($)", color = TextSecondary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = ElementColor,
                                focusedContainerColor = CardColor,
                                unfocusedContainerColor = CardColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("add_amount_input")
                        )

                        // Income / Expense selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isIncome) SuccessGreen.copy(alpha = 0.2f) else CardColor)
                                    .border(1.dp, if (isIncome) SuccessGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { isIncome = true }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ingreso (+)",
                                    color = if (isIncome) SuccessGreen else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (!isIncome) ErrorRed.copy(alpha = 0.2f) else CardColor)
                                    .border(1.dp, if (!isIncome) ErrorRed else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { isIncome = false }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Egreso (-)",
                                    color = if (!isIncome) ErrorRed else TextSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Category selection row
                        Text(text = "Selecciona Categoría", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(categories) { cat ->
                                val isSel = category == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PrimaryBlue else ElementColor)
                                        .clickable { category = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSel) Color.Black else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Cost center selector for business
                        if (!isIncome) {
                            Text(text = "Centro de Costos (Modo Pro)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val ccList = listOf("Ninguno", "IA", "VPS", "Hosting", "Dominios", "Publicidad Meta", "Google Ads", "Herramientas SaaS", "Automatizaciones", "Software")
                                items(ccList) { cc ->
                                    val isSelectedCc = (cc == "Ninguno" && selectedCostCenter == null) || (selectedCostCenter == cc)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelectedCc) PrimaryBlue.copy(alpha = 0.2f) else CardColor)
                                            .border(1.dp, if (isSelectedCc) PrimaryBlue else Color.Transparent, RoundedCornerShape(8.dp))
                                            .clickable { selectedCostCenter = if (cc == "Ninguno") null else cc }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cc,
                                            color = if (isSelectedCc) PrimaryBlue else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = amount.toDoubleOrNull() ?: 0.0
                                if (title.isNotBlank() && amt > 0) {
                                    viewModel.addTransaction(
                                        title = title,
                                        amount = amt,
                                        category = category,
                                        isIncome = isIncome,
                                        paymentMethod = paymentMethod,
                                        costCenter = selectedCostCenter
                                    )
                                    onSuccess()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_manual_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            enabled = title.isNotBlank() && amount.isNotBlank()
                        ) {
                            Text("Guardar Movimiento", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                1 -> {
                    // AI Scanning Simulators (Preset receipt upload layout)
                    Text(
                        text = "Escanear Comprobante con IA",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Sube o selecciona un comprobante digital de negocios para extraer automáticamente el concepto, monto y categoría usando Inteligencia Artificial.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Grid of presets that mimic high-fidelity scanned invoices
                    Text(text = "Selecciona un Comprobante para Escanear", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    
                    val invoicePresets = listOf(
                        Triple("AWS Invoice EC2", "$45.20", "AWS-BILL-2026.png"),
                        Triple("ChatGPT Plus", "$20.00", "OPENAI-RECEIPT.png"),
                        Triple("Stripe Payout", "$1,450.00", "STRIPE-TRANSFER.png"),
                        Triple("Meta Ads Manager", "$120.00", "FACEBOOK-ADS-INVOICE.png")
                    )

                    invoicePresets.forEach { (name, amount, fileName) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable {
                                    // Generate a mock Bitmap of custom painted text & scan it!
                                    val bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bitmap)
                                    val paint = Paint().apply { color = android.graphics.Color.BLACK }
                                    canvas.drawRect(0f, 0f, 120f, 120f, paint)
                                    viewModel.processReceiptImage(bitmap)
                                },
                            colors = CardDefaults.cardColors(containerColor = CardColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = "Receipt",
                                        tint = PrimaryBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = fileName, color = TextSecondary, fontSize = 10.sp)
                                }
                                Text(text = amount, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button to simulate upload raw file
                    Button(
                        onClick = {
                            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                            viewModel.processReceiptImage(bitmap)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElementColor)
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Upload")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subir Foto de Boleta / Captura")
                    }
                }

                2 -> {
                    // Voice Dictation Box and Presets
                    Text(
                        text = "Dictar Movimiento con IA",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Puedes dictar de forma natural tu gasto o ingreso. La IA de Control AI Pro interpretará el monto, creará el título y asignará el centro de costos automáticamente.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    var dictationText by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = dictationText,
                        onValueChange = { dictationText = it },
                        placeholder = { Text("Ej. Gasté 15 mil pesos en combustible, o Recibí un cobro de Stripe por 120 dólares...", color = TextSecondary) },
                        label = { Text("Tu Mensaje Dictado", color = TextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(bottom = 16.dp)
                            .testTag("dictation_text_input"),
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.processVoiceDictation(dictationText)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.Black),
                            enabled = dictationText.isNotBlank()
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Parse")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Interpretar IA")
                        }
                    }

                    // Dictation presets
                    Text(text = "Presets rápidos de voz:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                    
                    val voicePresets = listOf(
                        "Gasté 15 mil pesos en combustible",
                        "Recibí pago de Stripe de 120 dólares",
                        "Compré licencia de IntelliJ por 89 dólares",
                        "Pagué servidor mensual en AWS por 45 dólares"
                    )

                    voicePresets.forEach { preset ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    dictationText = preset
                                    viewModel.processVoiceDictation(preset)
                                },
                            colors = CardDefaults.cardColors(containerColor = CardColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.KeyboardVoice, contentDescription = "mic", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "\"$preset\"", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
