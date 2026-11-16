package com.androidproject.domain

import com.androidproject.data.local.ExpenseEntity
import kotlin.math.abs

// Two expenses count as a possible duplicate when the same person paid the same amount
// for the same thing in the same group, less than a day apart.
const val DUPLICATE_WINDOW_MS = 24 * 60 * 60 * 1000L

fun isPossibleDuplicate(new: ExpenseEntity, old: ExpenseEntity): Boolean {
    return new.id != old.id &&
        new.groupId == old.groupId &&
        new.paidByUserId == old.paidByUserId &&
        abs(new.amount - old.amount) < 0.005 &&
        new.title.trim().equals(old.title.trim(), ignoreCase = true) &&
        abs(new.timestamp - old.timestamp) <= DUPLICATE_WINDOW_MS
}

// Returns the closest match in time, or null if there is none
fun findPossibleDuplicate(new: ExpenseEntity, existing: List<ExpenseEntity>): ExpenseEntity? {
    return existing
        .filter { isPossibleDuplicate(new, it) }
        .minByOrNull { abs(new.timestamp - it.timestamp) }
}
