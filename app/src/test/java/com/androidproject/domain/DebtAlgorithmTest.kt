package com.androidproject.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class DebtAlgorithmTest {

    private val algorithm = DebtAlgorithm()

    @Test
    fun equalSplit_betweenThree_twoPeopleOwePayer() {
        val expense = Expense("A", 300.0, mapOf("A" to 100.0, "B" to 100.0, "C" to 100.0))

        val result = algorithm.calculateSettlements(listOf(expense))

        assertEquals(2, result.size)
        assertEquals(200.0, result.sumOf { it.amount }, 0.001)
        result.forEach { assertEquals("A", it.creditorId) }
    }

    @Test
    fun balances_alwaysSumToZero() {
        val expenses = listOf(
            Expense("A", 90.0, mapOf("A" to 30.0, "B" to 30.0, "C" to 30.0)),
            Expense("B", 60.0, mapOf("A" to 30.0, "B" to 30.0))
        )

        val balances = algorithm.calculateBalances(expenses)

        assertEquals(0.0, balances.values.sum(), 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun splitsNotMatchingAmount_areRejected() {
        val expense = Expense("A", 100.0, mapOf("A" to 30.0, "B" to 30.0))
        algorithm.calculateSettlements(listOf(expense))
    }
}
