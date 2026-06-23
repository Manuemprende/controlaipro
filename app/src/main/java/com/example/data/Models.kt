package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String, // e.g., "Suscripciones", "Transporte", "Comida", "IA", "VPS"
    val date: Long = System.currentTimeMillis(),
    val isIncome: Boolean,
    val paymentMethod: String = "Tarjeta",
    val costCenter: String? = null // For pro mode: "IA", "VPS", "Hosting", "Dominios", etc.
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date(date))
        }
}

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val isAnnual: Boolean = false,
    val nextBillingDate: String, // e.g., "27/06/2026"
    val status: String = "Activo", // "Activo" or "Pausado"
    val category: String, // e.g., "Herramientas IA", "Software", "VPS", "Hosting"
    val notifyDaysBefore: Int = 5,
    val notifyEnabled: Boolean = true
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
}

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
