package com.androidproject.domain

import kotlin.math.min
import kotlin.math.round

data class Expense(
    val payerId: String,
    val amount: Double,
    val splits: Map<String, Double>
)

data class DebtSettlement(
    val debtorId: String,
    val creditorId: String,
    val amount: Double
)

class DebtAlgorithm {

    fun calculateSettlements(expenses: List<Expense>): List<DebtSettlement> {
        val balances = calculateBalances(expenses)
        return simplifyDebts(balances)
    }

    internal fun calculateBalances(expenses: List<Expense>): Map<String, Double> {
        val balances = mutableMapOf<String, Double>()

        for (expense in expenses) {
            if (expense.splits.isEmpty()) continue

            balances[expense.payerId] = balances.getOrDefault(expense.payerId, 0.0) + expense.amount

            for ((participantId, owedAmount) in expense.splits) {
                balances[participantId] = balances.getOrDefault(participantId, 0.0) - owedAmount
            }
        }

        return balances
    }

    private fun simplifyDebts(balances: Map<String, Double>): List<DebtSettlement> {
        val settlements = mutableListOf<DebtSettlement>()

        val creditors = balances.filter { it.value > 0.01 }.map { it.key to it.value }.toMutableList()
        val debtors = balances.filter { it.value < -0.01 }.map { it.key to -it.value }.toMutableList()

        var i = 0
        var j = 0

        while (i < creditors.size && j < debtors.size) {
            val creditor = creditors[i]
            val debtor = debtors[j]

            val settleAmount = min(creditor.second, debtor.second)
            val roundedAmount = round(settleAmount * 100) / 100.0

            settlements.add(
                DebtSettlement(
                    debtorId = debtor.first,
                    creditorId = creditor.first,
                    amount = roundedAmount
                )
            )

            creditors[i] = creditor.first to (creditor.second - settleAmount)
            debtors[j] = debtor.first to (debtor.second - settleAmount)

            if (creditors[i].second < 0.01) i++
            if (debtors[j].second < 0.01) j++
        }

        return settlements
    }
}
