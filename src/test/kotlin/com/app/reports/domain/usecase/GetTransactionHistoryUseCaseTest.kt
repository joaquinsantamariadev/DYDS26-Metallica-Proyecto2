package com.app.reports.domain.usecase

import com.app.reports.data.repository.ReportsRepositoryFake
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.TransactionHistoryEntry
import com.app.reports.domain.usecase.GetTransactionHistoryUseCaseImpl
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetTransactionHistoryUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetTransactionHistoryUseCaseImpl(repo)

    @Test
    fun returnsTransactionsFromRepository() = runBlocking {
        val expected = listOf(
            TransactionHistoryEntry(1L, LocalDateTime.now(), emptyList(), 100.0)
        )
        repo.transactionHistoryResult = expected

        assertEquals(expected, useCase(ReportFilters.default(), 0))
    }

    @Test
    fun passesCorrectLimitAndOffset() = runBlocking {
        useCase(ReportFilters.default(), 2)

        assertEquals(GetTransactionHistoryUseCaseImpl.HISTORY_PAGE_SIZE, repo.capturedLimit)
        assertEquals(2 * GetTransactionHistoryUseCaseImpl.HISTORY_PAGE_SIZE, repo.capturedOffset)
    }

    @Test
    fun passesFiltersToRepository() = runBlocking {
        val filters = ReportFilters.default()
        useCase(filters, 0)

        assertEquals(filters, repo.capturedHistoryFilters)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase(ReportFilters.default(), 0) }
    }
}
