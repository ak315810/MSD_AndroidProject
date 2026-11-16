
package com.androidproject.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit = {}
) {
    var notificationsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var expensesEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var paymentsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var groupUpdatesEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var remindersEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notification Settings")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Manage your notifications",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Choose which updates you want to receive.",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            NotificationSettingRow(
                title = "Enable notifications",
                description = "Turn all notifications on or off",
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->
                    notificationsEnabled = enabled
                }
            )

            HorizontalDivider()

            Text(
                text = "Notification categories",
                style = MaterialTheme.typography.titleMedium
            )

            NotificationSettingRow(
                title = "New expenses",
                description = "When someone adds a new expense",
                checked = expensesEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { expensesEnabled = it }
            )

            NotificationSettingRow(
                title = "Payments",
                description = "Updates about completed payments",
                checked = paymentsEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { paymentsEnabled = it }
            )

            NotificationSettingRow(
                title = "Group updates",
                description = "When someone joins or updates a group",
                checked = groupUpdatesEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { groupUpdatesEnabled = it }
            )

            NotificationSettingRow(
                title = "Repayment reminders",
                description = "Reminders about pending repayments",
                checked = remindersEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { remindersEnabled = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sensitive financial information is hidden " +
                        "from notifications displayed on the lock screen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun NotificationSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
