
package com.androidproject.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenuAnchorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepaymentReminderScreen(
    onBackClick: () -> Unit = {},
    onReminderSent: () -> Unit = {}
) {
    val groups = listOf(
        "Erasmus Flat",
        "Copenhagen Trip",
        "Birthday Party"
    )

    val members = listOf(
        "Alex",
        "Jamie",
        "Taylor"
    )

    var selectedGroup by rememberSaveable {
        mutableStateOf(groups.first())
    }

    var selectedMember by rememberSaveable {
        mutableStateOf("")
    }

    var message by rememberSaveable {
        mutableStateOf("Hey! Just a friendly reminder about the pending repayment.")
    }

    var groupExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var memberExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var reminderSent by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Repayment Reminder")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
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

            Text("Send a friendly repayment reminder.")

            ExposedDropdownMenuBox(
                expanded = groupExpanded,
                onExpandedChange = {
                    groupExpanded = !groupExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select group") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = groupExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = groupExpanded,
                    onDismissRequest = {
                        groupExpanded = false
                    }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group) },
                            onClick = {
                                selectedGroup = group
                                selectedMember = ""
                                reminderSent = false
                                groupExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = memberExpanded,
                onExpandedChange = {
                    memberExpanded = !memberExpanded
                }
            ) {
                OutlinedTextField(
                    value = selectedMember,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select member") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = memberExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = memberExpanded,
                    onDismissRequest = {
                        memberExpanded = false
                    }
                ) {
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member) },
                            onClick = {
                                selectedMember = member
                                reminderSent = false
                                memberExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                    reminderSent = false
                },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    reminderSent = true
                    onReminderSent()
                },
                enabled = selectedMember.isNotBlank() &&
                        message.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reminder")
            }

            if (reminderSent) {
                Text("Reminder sent successfully!")
            }
        }
    }
}
