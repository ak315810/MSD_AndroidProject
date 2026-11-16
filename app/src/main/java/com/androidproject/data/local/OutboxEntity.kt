package com.androidproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outbox")
data class OutboxEntity(
    @PrimaryKey val expenseId: String,
    val attempts: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)