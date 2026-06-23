package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.gemini.AnalyzedReceipt
import com.example.data.gemini.FinanceAIHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(database.financeDao())

    // Raw Room flows
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<Subscription>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<Alert>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters and states
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedTypeFilter = MutableStateFlow<String?>(null) // "Ingreso", "Egreso", null
    val selectedPeriodFilter = MutableStateFlow("Todos") // "Hoy", "7 días", "30 días", "90 días", "Todos"

    // AI States
    val aiInsight = MutableStateFlow("Cargando análisis inteligente con IA...")
    val isAnalyzing = MutableStateFlow(false)
    val chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "¡Hola! Soy tu asistente financiero Control AI Pro. ¿En qué puedo ayudarte hoy?" to false
        )
    )
    val isChatLoading = MutableStateFlow(false)

    // Result of voice or receipt analysis
    val aiResultPreview = MutableStateFlow<AnalyzedReceipt?>(null)
    val isProcessingAiInput = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
            generateAiInsight()
            checkUpcomingSubscriptionRenewals(application)
        }
    }

    // Filtered transactions
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        searchQuery,
        selectedCategoryFilter,
        selectedTypeFilter,
        selectedPeriodFilter
    ) { transList, query, cat, type, period ->
        var list = transList

        // Search Query
        if (query.isNotEmpty()) {
            list = list.filter { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
        }

        // Category
        if (cat != null) {
            list = list.filter { it.category.equals(cat, ignoreCase = true) || it.costCenter?.equals(cat, ignoreCase = true) == true }
        }

        // Type
        if (type != null) {
            val isIncomeReq = type == "Ingreso"
            list = list.filter { it.isIncome == isIncomeReq }
        }

        // Period filter
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        list = when (period) {
            "Hoy" -> list.filter { now - it.date < dayMs }
            "7 días" -> list.filter { now - it.date < 7 * dayMs }
            "30 días" -> list.filter { now - it.date < 30 * dayMs }
            "90 días" -> list.filter { now - it.date < 90 * dayMs }
            else -> list
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial calculations
    val totalIncome: StateFlow<Double> = transactions
        .map { list -> list.filter { it.isIncome }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = transactions
        .map { list -> list.filter { !it.isIncome }.sumOf { it.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSavings: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        (income - expense).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalMonthlySubscriptionPrice: StateFlow<Double> = subscriptions
        .map { list ->
            list.filter { it.status == "Activo" }.sumOf {
                if (it.isAnnual) it.price / 12.0 else it.price
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalAnnualSubscriptionPrice: StateFlow<Double> = subscriptions
        .map { list ->
            list.filter { it.status == "Activo" }.sumOf {
                if (it.isAnnual) it.price else it.price * 12.0
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Transaction modification
    fun addTransaction(title: String, amount: Double, category: String, isIncome: Boolean, paymentMethod: String = "Tarjeta", costCenter: String? = null) {
        viewModelScope.launch {
            val transaction = Transaction(
                title = title,
                amount = amount,
                category = category,
                isIncome = isIncome,
                paymentMethod = paymentMethod,
                costCenter = costCenter
            )
            repository.insertTransaction(transaction)
            generateAiInsight()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            generateAiInsight()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            generateAiInsight()
        }
    }

    fun duplicateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val duplicated = transaction.copy(id = 0, date = System.currentTimeMillis())
            repository.insertTransaction(duplicated)
            generateAiInsight()
        }
    }

    // Subscriptions modification
    fun addSubscription(
        name: String, 
        price: Double, 
        isAnnual: Boolean, 
        nextBillingDate: String, 
        status: String = "Activo", 
        category: String,
        notifyDaysBefore: Int = 5,
        notifyEnabled: Boolean = true
    ) {
        viewModelScope.launch {
            val subscription = Subscription(
                name = name,
                price = price,
                isAnnual = isAnnual,
                nextBillingDate = nextBillingDate,
                status = status,
                category = category,
                notifyDaysBefore = notifyDaysBefore,
                notifyEnabled = notifyEnabled
            )
            repository.insertSubscription(subscription)
            checkUpcomingSubscriptionRenewals(getApplication())
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.updateSubscription(subscription)
            checkUpcomingSubscriptionRenewals(getApplication())
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    fun checkUpcomingSubscriptionRenewals(context: Context) {
        viewModelScope.launch {
            // Get current list of subscriptions
            val list = repository.allSubscriptions.firstOrNull() ?: subscriptions.value
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time

            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

            list.forEach { sub ->
                if (sub.status == "Activo" && sub.notifyEnabled) {
                    try {
                        val billingDate = sdf.parse(sub.nextBillingDate)
                        if (billingDate != null) {
                            val diffMs = billingDate.time - today.time
                            val diffDays = diffMs / (1000 * 60 * 60 * 24)

                            if (diffDays in 0..sub.notifyDaysBefore) {
                                val alertTitle = "¡Suscripción por Vencer!"
                                val alertDesc = "Tu servicio ${sub.name} cobrará $${sub.price} en $diffDays días (el ${sub.nextBillingDate}). Cancélalo antes si lo deseas."

                                // Check if we already created a recent identical alert to avoid duplicate spam
                                val existingAlerts = alerts.value
                                val hasDuplicate = existingAlerts.any { 
                                    it.title == alertTitle && it.description.contains(sub.name) && (System.currentTimeMillis() - it.timestamp < 12 * 60 * 60 * 1000L) 
                                }

                                if (!hasDuplicate) {
                                    // 1. In-app alert
                                    repository.insertAlert(
                                        Alert(
                                            title = alertTitle,
                                            description = alertDesc,
                                            timestamp = System.currentTimeMillis()
                                        )
                                    )

                                    // 2. Native push notification
                                    NotificationHelper.showNotification(
                                        context = context,
                                        id = sub.id + 10000,
                                        title = "Control AI Pro: $alertTitle",
                                        message = "Tu servicio ${sub.name} cobra $${sub.price} en $diffDays días."
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun triggerDemoNotification(context: Context) {
        viewModelScope.launch {
            // Create a custom demo in-app alert
            repository.insertAlert(
                Alert(
                    title = "Notificación de Prueba Exitosa",
                    description = "Se ha activado correctamente una alerta push nativa para tus suscripciones.",
                    timestamp = System.currentTimeMillis()
                )
            )

            // Trigger real system notification
            NotificationHelper.showNotification(
                context = context,
                id = 99999,
                title = "Control AI Pro: Alerta Push Activa",
                message = "¡Notificaciones nativas configuradas! Recibirás alertas 5 días antes de vencer."
            )
        }
    }

    // Goals modification
    fun addGoal(name: String, targetAmount: Double, currentAmount: Double = 0.0) {
        viewModelScope.launch {
            repository.insertGoal(Goal(name = name, targetAmount = targetAmount, currentAmount = currentAmount))
        }
    }

    fun addProgressToGoal(goal: Goal, amount: Double) {
        viewModelScope.launch {
            val updated = goal.copy(currentAmount = (goal.currentAmount + amount).coerceIn(0.0, goal.targetAmount))
            repository.updateGoal(updated)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Alerts modification
    fun markAlertAsRead(alert: Alert) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(isRead = true))
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }

    // AI actions
    fun generateAiInsight() {
        viewModelScope.launch {
            isAnalyzing.value = true
            val list = transactions.value
            val subsCount = subscriptions.value.size
            val subsPrice = totalMonthlySubscriptionPrice.value
            val insight = FinanceAIHelper.getFinancialInsight(list, subsCount, subsPrice)
            aiInsight.value = insight
            isAnalyzing.value = false
        }
    }

    fun sendChatMessage(message: String) {
        if (message.isBlank()) return
        chatMessages.value = chatMessages.value + (message to true)
        viewModelScope.launch {
            isChatLoading.value = true
            val list = transactions.value
            val subsCount = subscriptions.value.size
            val subsPrice = totalMonthlySubscriptionPrice.value
            val reply = FinanceAIHelper.getFinancialInsight(list, subsCount, subsPrice, message)
            chatMessages.value = chatMessages.value + (reply to false)
            isChatLoading.value = false
        }
    }

    fun processReceiptImage(bitmap: Bitmap) {
        viewModelScope.launch {
            isProcessingAiInput.value = true
            val result = FinanceAIHelper.analyzeReceipt(bitmap)
            aiResultPreview.value = result
            isProcessingAiInput.value = false
        }
    }

    fun processVoiceDictation(text: String) {
        viewModelScope.launch {
            isProcessingAiInput.value = true
            val result = FinanceAIHelper.parseVoiceInput(text)
            aiResultPreview.value = result
            isProcessingAiInput.value = false
        }
    }

    fun confirmAiResult() {
        val result = aiResultPreview.value ?: return
        addTransaction(
            title = result.title,
            amount = result.amount,
            category = result.category,
            isIncome = result.isIncome,
            costCenter = result.costCenter
        )
        // Also add a system alert confirming AI extraction
        viewModelScope.launch {
            repository.insertAlert(
                Alert(
                    title = "Movimiento Registrado con IA",
                    description = "Se detectó '${result.title}' por valor de $${result.amount} en la categoría '${result.category}'.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        aiResultPreview.value = null
    }

    fun cancelAiResult() {
        aiResultPreview.value = null
    }
}
