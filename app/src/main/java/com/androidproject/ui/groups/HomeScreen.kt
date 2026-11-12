
package com.androidproject.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidproject.ui.theme.AndroidProjectTheme

@Composable
fun HomeScreen(
    onMyGroupsClick: () -> Unit,
    onAddGroupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Manage your shared expenses with friends.",
            style = MaterialTheme.typography.bodyLarge
        )

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
