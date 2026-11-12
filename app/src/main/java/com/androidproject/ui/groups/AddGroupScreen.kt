
package com.androidproject.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidproject.ui.theme.AndroidProjectTheme

@Composable
fun AddGroupScreen(
    onCreateGroup: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isValid = groupName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Create New Group",
            style = MaterialTheme.typography.headlineLarge
        )

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (optional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onCreateGroup(
                    groupName.trim(),
                    description.trim()
                )
            },
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Group")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddGroupScreenPreview() {
    AndroidProjectTheme {
        AddGroupScreen(
            onCreateGroup = { _, _ -> },
            onCancel = {}
        )
    }
}
