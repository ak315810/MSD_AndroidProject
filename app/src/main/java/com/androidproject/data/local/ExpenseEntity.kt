package com.androidproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val title: String,
    val amount: Double,
    val paidByUserId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING" // "PENDING" or "SYNCED"
)