package com.androidproject.ui.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(
    onSaveExpense: (description: String, amount: Double) -> Unit,
    onNavigateBack: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var isAmountError by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }

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
                    // validate: is the number correct
                    isAmountError = newValue.isNotEmpty() && newValue.toDoubleOrNull() == null
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
                val amount = amountText.toDoubleOrNull()
                if (amount != null && amount > 0 && !isAmountError) {
                    onSaveExpense(description, amount)
                    onNavigateBack()
                } else {
                    isAmountError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Save expense")
        }
    }
}