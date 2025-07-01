package com.deeksha.avrentertainment.utils

import com.deeksha.avrentertainment.models.Budget
import com.deeksha.avrentertainment.models.BudgetItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BudgetUploader {
    private val db = FirebaseFirestore.getInstance()
    private val budgetsCollection = db.collection("budgets")

    suspend fun uploadPreProductionBudget() = withContext(Dispatchers.IO) {
        try {
            val items = listOf(
                BudgetItem(1, "Office Expenses", 15000.0, 8, 120000.0, 8),
                BudgetItem(2, "Stationary", 10000.0, 2, 20000.0, 2),
                BudgetItem(3, "Printer", 15000.0, 1, 15000.0, 1),
                BudgetItem(4, "Poster Designer", 150000.0, 1, 150000.0, 1),
                BudgetItem(5, "Final Reckie", 50000.0, 1, 50000.0, 1),
                BudgetItem(6, "Photoshoot", 100000.0, 1, 100000.0, 1),
                BudgetItem(7, "Costume", 300000.0, 1, 300000.0, 1),
                BudgetItem(8, "Properties", 300000.0, 1, 300000.0, 1),
                BudgetItem(9, "Colour Pallet", 150000.0, 1, 150000.0, 1)
            )
            
            val budget = Budget(
                category = "Pre Production",
                items = items,
                totalAmount = items.sumOf { item -> item.amount }
            )

            val docRef = budgetsCollection.document()
            val budgetWithId = budget.copy(id = docRef.id)
            docRef.set(budgetWithId).await()
            "Budget uploaded successfully with ID: ${docRef.id}"
        } catch (e: Exception) {
            "Error uploading budget: ${e.message}"
        }
    }
} 