package com.androidproject.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.androidproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    state: GroupDetailsUiState,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.group_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_expense_cd)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (state.isOffline) {
                OfflineBanner(pendingCount = state.pendingCount)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!state.isLoading) {
                Text(text = state.groupName, style = MaterialTheme.typography.headlineMedium)
                if (state.description.isNotBlank()) {
                    Text(text = state.description, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                state.isLoading -> LoadingContent()
                state.isEmpty -> EmptyContent(onAddExpense = onAddExpense)
                else -> ExpenseList(expenses = state.expenses)
            }
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: ExpenseUi,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = stringResource(R.string.paid_by, expense.paidBy),
                    style = MaterialTheme.typography.bodySmall
                )
                when (expense.syncStatus) {
                    SyncStatus.PENDING -> Text(
                        text = stringResource(R.string.pending_sync),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                    SyncStatus.FAILED -> Text(
                        text = stringResource(R.string.sync_failed),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                    SyncStatus.SYNCED -> Unit
                }
            }
            Text(
                text = stringResource(R.string.amount_format, expense.amount),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun ExpenseList(expenses: List<ExpenseUi>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(expenses, key = { it.id }) { expense ->
            ExpenseItem(expense = expense)
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.loading_text),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyContent(onAddExpense: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.empty_expenses))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAddExpense) {
            Text(text = stringResource(R.string.add_expense_button))
        }
    }
}

@Composable
private fun OfflineBanner(pendingCount: Int) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.offline_banner_message, pendingCount),
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
