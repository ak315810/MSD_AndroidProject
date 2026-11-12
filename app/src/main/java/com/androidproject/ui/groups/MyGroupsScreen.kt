
package com.androidproject.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
        Text(
            text = "My Groups",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddGroupClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Group")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (groups.isEmpty()) {
            Text("You don't belong to any groups yet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups, key = { it.id }) { group ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onGroupClick(group.id)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
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
