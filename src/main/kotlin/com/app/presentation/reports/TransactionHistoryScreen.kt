package com.app.presentation.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.presentation.reports.components.DateRangePicker
import com.app.presentation.reports.components.TransactionTable

@Composable
fun TransactionHistoryScreen(viewModel: TransactionHistoryViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        DateRangePicker(
            filters = state.filters,
            onFiltersChanged = { viewModel.onFiltersChanged(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading && state.transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.error}")
            }
        } else {
            TransactionTable(
                transactions = state.transactions,
                hasNextPage = state.hasNextPage,
                onLoadMore = { viewModel.loadNextPage() },
                isLoadingMore = state.isLoading
            )
        }
    }
}
