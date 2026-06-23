package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isScanningBiometric by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableFloatStateOf(0f) }
    
    val scope = rememberCoroutineScope()
    
    // Golden Aura Color (Sayayin Mode)
    val SayayinGold = Color(0xFFFFC107)
    val GlowBlue = Color(0xFF00E5FF)
    
    // Pulse animation for login aura
    val infiniteTransition = rememberInfiniteTransition(label = "aura_pulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )
    
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Handle pin entry check
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            if (pin == "1234") {
                errorMessage = ""
                onLoginSuccess()
            } else {
                errorMessage = "PIN Incorrecto"
                delay(1000)
                pin = ""
                errorMessage = ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp)
            .testTag("login_screen_container")
    ) {
        // Core Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // 1. Branding Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .drawWithContent {
                            drawContent()
                            // Beautiful outer aura glow
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GlowBlue.copy(alpha = 0.5f * auraAlpha),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension * 0.75f * auraScale
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        PrimaryBlue,
                                        TertiaryDark
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "CONTROL AI PRO",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
                
                Text(
                    text = "SISTEMA INTELIGENTE SAYAYIN ELITE",
                    color = SayayinGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 2. PIN Status Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secured",
                        tint = if (errorMessage.isNotEmpty()) ErrorRed else SayayinGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (errorMessage.isNotEmpty()) errorMessage else "INGRESA TU PIN DE SEGURIDAD",
                        color = if (errorMessage.isNotEmpty()) ErrorRed else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4 PIN Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = pin.length > i
                        val isError = errorMessage.isNotEmpty()
                        
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isError) ErrorRed 
                                    else if (isFilled) SayayinGold 
                                    else Color.Transparent
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isError) ErrorRed 
                                            else if (isFilled) SayayinGold 
                                            else TextSecondary.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .drawWithContent {
                                    drawContent()
                                    if (isFilled && !isError) {
                                        // Golden glowing aura surrounding filled dot
                                        drawCircle(
                                            color = SayayinGold.copy(alpha = 0.4f),
                                            radius = size.minDimension * 0.9f * auraScale
                                        )
                                    }
                                }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "PIN demo: 1234",
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. Ultra-Premium Smart Keypad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Number Rows
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9")
                )

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { digit ->
                            KeypadButton(
                                text = digit,
                                onClick = {
                                    if (pin.length < 4 && !isScanningBiometric) {
                                        pin += digit
                                    }
                                }
                            )
                        }
                    }
                }

                // Last Row: Biometric, 0, Backspace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Fingerprint Biometric Bypass
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ElementColor.copy(alpha = 0.6f))
                            .clickable {
                                if (!isScanningBiometric) {
                                    scope.launch {
                                        isScanningBiometric = true
                                        scanProgress = 0f
                                        while (scanProgress < 1f) {
                                            delay(50)
                                            scanProgress += 0.05f
                                        }
                                        delay(200)
                                        onLoginSuccess()
                                    }
                                }
                            }
                            .testTag("biometric_login_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Huella",
                            tint = GlowBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Center: 0
                    KeypadButton(
                        text = "0",
                        onClick = {
                            if (pin.length < 4 && !isScanningBiometric) {
                                pin += "0"
                            }
                        }
                    )

                    // Right: Backspace
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ElementColor.copy(alpha = 0.6f))
                            .clickable {
                                if (pin.isNotEmpty() && !isScanningBiometric) {
                                    pin = pin.dropLast(1)
                                }
                            }
                            .testTag("backspace_login_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Borrar",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Biometric Scanning Overlay Dialog
        if (isScanningBiometric) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .padding(16.dp)
                        .drawWithContent {
                            drawContent()
                            // Glow board
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(GlowBlue, SayayinGold)
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = CardColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ESCÁNER BIOMÉTRICO",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Acceso Rápido Sayayin Pro",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(100.dp)
                        ) {
                            // Circular pulse aura
                            CircularProgressIndicator(
                                progress = { scanProgress },
                                modifier = Modifier.size(90.dp),
                                color = GlowBlue,
                                strokeWidth = 4.dp,
                                trackColor = ElementColor
                            )
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint",
                                tint = GlowBlue,
                                modifier = Modifier
                                    .size(56.dp)
                                    .drawWithContent {
                                        drawContent()
                                        // Pulse golden overlay on the fingerprint icon
                                        drawCircle(
                                            color = SayayinGold.copy(alpha = 0.3f * auraAlpha),
                                            radius = size.minDimension * 0.6f * auraScale
                                        )
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Verificando Identidad...",
                            color = SayayinGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(CardColor)
            .clickable { onClick() }
            .border(1.dp, ElementColor, CircleShape)
            .testTag("keypad_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
