package com.androidproject.ui.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidproject.R
import com.androidproject.data.local.ExpenseEntity
import java.text.DateFormat
import java.util.Date

// Shows the new expense next to the one already saved so the user can compare them
@Composable
fun DuplicateExpenseDialog(
    warning: DuplicateWarning,
    onSaveAnyway: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.duplicate_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.duplicate_message))
                ExpensePreviewCard(stringResource(R.string.duplicate_new_expense), warning.newExpense)
                ExpensePreviewCard(
                    stringResource(R.string.duplicate_existing_expense),
                    warning.existingExpense
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSaveAnyway) {
                Text(stringResource(R.string.duplicate_save_anyway))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun ExpensePreviewCard(label: String, expense: ExpenseEntity) {
    // Highlighted background because these are the fields that match
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                expense.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(stringResource(R.string.amount_format, expense.amount))
            Text(stringResource(R.string.paid_by, expense.paidByUserId))
            Text(
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(Date(expense.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun DuplicateExpenseDialogPreview() {
    val old = ExpenseEntity("1", "g1", "Pizza", 240.0, "Maria", timestamp = 1_700_000_000_000)
    MaterialTheme {
        DuplicateExpenseDialog(
            warning = DuplicateWarning(
                newExpense = old.copy(id = "2", timestamp = old.timestamp + 600_000),
                existingExpense = old
            ),
            onSaveAnyway = {},
            onCancel = {}
        )
    }
}
