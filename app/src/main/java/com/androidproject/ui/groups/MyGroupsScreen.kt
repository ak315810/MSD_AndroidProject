
package com.androidproject.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidproject.ui.theme.AndroidProjectTheme

data class Group(
    val id: Int,
    val name: String,
    val members: Int
)

@Composable
fun MyGroupsScreen(
    groups: List<Group>,
    onGroupClick: (Int) -> Unit,
    onAddGroupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // Header with title and add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Groups",
                style = MaterialTheme.typography.headlineLarge
            )

            FilledIconButton(
                onClick = onAddGroupClick,
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Empty state
        if (groups.isEmpty()) {
            Text(
                text = "You don't belong to any groups yet.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {

            // List of groups
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups, key = { it.id }) { group ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onGroupClick(group.id)
                            },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // Group icon
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👥")
                                }
                            }

                            // Group information
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "${group.members} members",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Navigation indicator
                            Text(
                                text = "›",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyGroupsScreenPreview() {
    AndroidProjectTheme {
        MyGroupsScreen(
            groups = listOf(
                Group(1, "Erasmus Flat", 4),
                Group(2, "Copenhagen Trip", 6),
                Group(3, "Birthday Party", 8)
            ),
            onGroupClick = {},
            onAddGroupClick = {}
        )
    }
}
