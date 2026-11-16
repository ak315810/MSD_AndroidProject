package com.androidproject.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(
    onSaveExpense: (description: String, amount: Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    // rememberSaveable so the form is not cleared when the phone is rotated
    var amountText by rememberSaveable { mutableStateOf("") }
    var isAmountError by rememberSaveable { mutableStateOf(false) }

    var description by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add expense",
            style = MaterialTheme.typography.headlineMedium
        )

        // add description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("What are you paying for") },
            modifier = Modifier.fillMaxWidth()
        )

        // validate amount
        OutlinedTextField(
            value = amountText,
            onValueChange = { newValue ->
                // no -values
                if (!newValue.contains("-")) {
                    amountText = newValue
                    // validate right away, 0 is not a valid amount either (NFR-U2)
                    isAmountError = newValue.isNotEmpty() && parseAmount(newValue) == null
                }
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = isAmountError,
            supportingText = {
                if (isAmountError) {
                    Text("Enter correct positive number")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val amount = parseAmount(amountText)
                if (amount != null && description.isNotBlank()) {
                    onSaveExpense(description.trim(), amount)
                    onNavigateBack()
                } else {
                    isAmountError = amount == null
                }
            },
            enabled = description.isNotBlank() && parseAmount(amountText) != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Save expense")
        }
    }
}

// Danish keyboards type "12,50", so accept a comma as decimal separator
internal fun parseAmount(text: String): Double? {
    val value = text.trim().replace(',', '.').toDoubleOrNull() ?: return null
    return if (value > 0) value else null
}
