package com.app.presentation.reports

import com.app.data.ReportsRepositoryFake
import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.TransactionHistoryEntry
import com.app.domain.usecase.report.GetTransactionHistoryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import java.time.LocalDateTime
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionHistoryViewModelTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetTransactionHistoryUseCase(repo)

    private fun buildViewModel() = TransactionHistoryViewModel(
        getTransactionHistoryUseCase = useCase,
        scope = CoroutineScope(UnconfinedTestDispatcher())
    )

    private fun transaction(id: Long, total: Double) = TransactionHistoryEntry(
        saleId = id, dateTime = LocalDateTime.now(), items = emptyList(), total = total
    )

    @Test
    fun initialState_loadsTransactionsAutomatically() {
        val expected = listOf(transaction(1L, 100.0))
        repo.transactionHistoryResult = expected

        val vm = buildViewModel()

        assertEquals(expected, vm.uiState.value.transactions)
        assertFalse(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun loadNextPage_accumulatesTransactions() {
        val page0 = (1..50).map { transaction(it.toLong(), 10.0) }
        repo.transactionHistoryResult = page0

        val vm = buildViewModel()
        assertTrue(vm.uiState.value.hasNextPage)

        val page1 = listOf(transaction(51L, 20.0))
        repo.transactionHistoryResult = page1

        vm.loadNextPage()

        assertEquals(51, vm.uiState.value.transactions.size)
        assertFalse(vm.uiState.value.hasNextPage)
    }

    @Test
    fun onFiltersChanged_resetsAndReloads() {
        repo.transactionHistoryResult = listOf(transaction(1L, 100.0))
        val vm = buildViewModel()

        val newFilters = ReportFilters.default().copy(from = ReportFilters.default().from.minusMonths(1))
        repo.transactionHistoryResult = listOf(transaction(2L, 200.0))
        vm.onFiltersChanged(newFilters)

        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals(2L, vm.uiState.value.transactions.first().saleId)
        assertEquals(newFilters, vm.uiState.value.filters)
    }

    @Test
    fun error_setsErrorStateAndEmptyTransactions() {
        repo.shouldThrowError = true

        val vm = buildViewModel()

        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.transactions.isEmpty())
    }

    @Test
    fun refresh_resetsAndReloads() {
        repo.transactionHistoryResult = listOf(transaction(1L, 100.0))
        val vm = buildViewModel()

        repo.transactionHistoryResult = listOf(transaction(2L, 200.0))
        vm.refresh()

        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals(2L, vm.uiState.value.transactions.first().saleId)
        assertEquals(0, vm.uiState.value.currentPage)
    }
}
