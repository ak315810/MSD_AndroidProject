package com.androidproject.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: String,
    val isRead: Boolean = false
)

// The sketch has a gear icon (S13) and a "+" button (S12) on this screen, so both
// destinations are passed in from the nav graph
@Composable
fun NotificationsScreen(
    onSettingsClick: () -> Unit = {},
    onSendReminderClick: () -> Unit = {}
) {

    val notifications = remember {
        mutableStateListOf(
            NotificationItem(
                1, "New group expense",
                "A new expense was added to Erasmus Flat.",
                "10 minutes ago", "💰"
            ),
            NotificationItem(
                2, "Payment received",
                "A group member marked a payment as completed.",
                "1 hour ago", "💸"
            ),
            NotificationItem(
                3, "New group member",
                "Someone joined Copenhagen Trip.",
                "Yesterday", "👥", true
            ),
            NotificationItem(
                4, "Repayment reminder",
                "You have a new repayment reminder.",
                "Yesterday", "🔔", true
            )
        )
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onSendReminderClick) {
                Icon(Icons.Filled.Add, contentDescription = "Send repayment reminder")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Notification settings")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$unreadCount unread notifications",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                TextButton(
                    onClick = {
                        notifications.indices.forEach { index ->
                            notifications[index] =
                                notifications[index].copy(isRead = true)
                        }
                    }
                ) {
                    Text("Mark all as read")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val index = notifications.indexOfFirst {
                                    it.id == notification.id
                                }

                                if (index != -1) {
                                    notifications[index] =
                                        notification.copy(isRead = true)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notification.isRead)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = notification.icon,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notification.title,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notification.message,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = notification.time,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            if (!notification.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
