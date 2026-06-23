package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class FinanceRepository(private val dao: FinanceDao) {

    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val allSubscriptions: Flow<List<Subscription>> = dao.getAllSubscriptions()
    val allGoals: Flow<List<Goal>> = dao.getAllGoals()
    val allAlerts: Flow<List<Alert>> = dao.getAllAlerts()

    suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        dao.deleteTransactionById(id)
    }

    suspend fun insertSubscription(subscription: Subscription) {
        dao.insertSubscription(subscription)
    }

    suspend fun updateSubscription(subscription: Subscription) {
        dao.updateSubscription(subscription)
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        dao.deleteSubscription(subscription)
    }

    suspend fun insertGoal(goal: Goal) {
        dao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        dao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        dao.deleteGoal(goal)
    }

    suspend fun insertAlert(alert: Alert) {
        dao.insertAlert(alert)
    }

    suspend fun updateAlert(alert: Alert) {
        dao.updateAlert(alert)
    }

    suspend fun deleteAlert(alert: Alert) {
        dao.deleteAlert(alert)
    }

    suspend fun seedDatabaseIfNeeded() {
        val transactionsList = dao.getAllTransactions().firstOrNull() ?: emptyList()
        if (transactionsList.isEmpty()) {
            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            // Seed Transactions
            val seedTransactions = listOf(
                Transaction(title = "Cobro Stripe", amount = 1200.0, category = "SaaS", date = now, isIncome = true, paymentMethod = "Transferencia"),
                Transaction(title = "Venta Suscripción SaaS", amount = 150.0, category = "SaaS", date = now - 3 * dayMs, isIncome = true, paymentMethod = "Stripe"),
                Transaction(title = "Servicio Consultoría", amount = 850.0, category = "Consultoría", date = now - 5 * dayMs, isIncome = true, paymentMethod = "Transferencia"),
                Transaction(title = "Compra Licencias JetBrains", amount = 89.0, category = "Software", date = now - dayMs, isIncome = false, paymentMethod = "Tarjeta", costCenter = "Software"),
                Transaction(title = "Cena con Cliente", amount = 45.0, category = "Comida", date = now - 2 * dayMs, isIncome = false, paymentMethod = "Efectivo"),
                Transaction(title = "Publicidad Facebook Ads", amount = 120.0, category = "SaaS", date = now, isIncome = false, paymentMethod = "Tarjeta", costCenter = "Publicidad Meta"),
                Transaction(title = "AWS Elastic Beanstalk", amount = 45.2, category = "VPS", date = now - 4 * dayMs, isIncome = false, paymentMethod = "Tarjeta", costCenter = "VPS"),
                Transaction(title = "Google Ads Campaña", amount = 85.0, category = "Publicidad", date = now - 6 * dayMs, isIncome = false, paymentMethod = "Tarjeta", costCenter = "Google Ads")
            )
            for (t in seedTransactions) {
                dao.insertTransaction(t)
            }

            // Seed Subscriptions
            val seedSubscriptions = listOf(
                Subscription(name = "Netflix", price = 12.99, isAnnual = false, nextBillingDate = "27/06/2026", status = "Activo", category = "Suscripciones"),
                Subscription(name = "ChatGPT Plus", price = 20.00, isAnnual = false, nextBillingDate = "01/07/2026", status = "Activo", category = "Herramientas IA"),
                Subscription(name = "Claude Pro", price = 20.00, isAnnual = false, nextBillingDate = "05/07/2026", status = "Activo", category = "Herramientas IA"),
                Subscription(name = "Google One", price = 1.99, isAnnual = false, nextBillingDate = "12/07/2026", status = "Activo", category = "Software"),
                Subscription(name = "AWS Cloud EC2", price = 35.00, isAnnual = false, nextBillingDate = "01/07/2026", status = "Activo", category = "VPS"),
                Subscription(name = "Dominios Namecheap", price = 15.00, isAnnual = true, nextBillingDate = "15/10/2026", status = "Activo", category = "Dominios")
            )
            for (s in seedSubscriptions) {
                dao.insertSubscription(s)
            }

            // Seed Goals
            val seedGoals = listOf(
                Goal(name = "Comprar notebook", targetAmount = 1500.0, currentAmount = 450.0),
                Goal(name = "Fondo de Emergencia", targetAmount = 2000.0, currentAmount = 800.0)
            )
            for (g in seedGoals) {
                dao.insertGoal(g)
            }

            // Seed Alerts
            val seedAlerts = listOf(
                Alert(title = "Suscripción próxima a vencer", description = "ChatGPT Plus se cobrará el 01/07/2026 por un valor de $20.00.", timestamp = now, isRead = false),
                Alert(title = "Alerta de Gasto VPS", description = "Tus servicios de AWS superaron el límite de alerta mensual de $30.00.", timestamp = now - dayMs, isRead = false)
            )
            for (a in seedAlerts) {
                dao.insertAlert(a)
            }
        }
    }
}
