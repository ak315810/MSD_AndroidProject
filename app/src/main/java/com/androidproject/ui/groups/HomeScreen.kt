
package com.androidproject.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidproject.ui.theme.AndroidProjectTheme

// Example data for the Home screen
data class GroupBalance(
    val groupName: String,
    val amount: Int
)

@Composable
fun HomeScreen(
    onMyGroupsClick: () -> Unit,
    onAddGroupClick: () -> Unit
) {
    // Temporary data until we connect the real database
    val balances = listOf(
        GroupBalance("Erasmus Flat", -150),
        GroupBalance("Copenhagen Trip", 400)
    )

    val totalBalance = balances.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "My debts / balance",
            style = MaterialTheme.typography.titleLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Overall balance",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${if (totalBalance > 0) "+" else ""}$totalBalance DKK",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        HorizontalDivider()

        // Display each group's balance
        balances.forEach { balance ->

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = balance.groupName,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = if (balance.amount < 0) {
                        "You owe ${-balance.amount} DKK"
                    } else {
                        "You are owed ${balance.amount} DKK"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onMyGroupsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Groups")
        }

        OutlinedButton(
            onClick = onAddGroupClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Group")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AndroidProjectTheme {
        HomeScreen(
            onMyGroupsClick = {},
            onAddGroupClick = {}
        )
    }
}
