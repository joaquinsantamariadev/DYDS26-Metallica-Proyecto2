package com.app.reports.presentation

import com.app.reports.data.repository.ReportsRepositoryFake
import com.app.reports.domain.entity.*
import com.app.reports.domain.usecase.GetMarginsUseCaseImpl
import com.app.reports.domain.usecase.GetProductRotationUseCaseImpl
import com.app.reports.domain.usecase.GetRevenueSummaryUseCaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    private val repo = ReportsRepositoryFake()
    private val getRevenue = GetRevenueSummaryUseCaseImpl(repo)
    private val getRotation = GetProductRotationUseCaseImpl(repo)
    private val getMargins = GetMarginsUseCaseImpl(repo)

    private fun buildViewModel() = StatisticsViewModel(
        getRevenueSummaryUseCase = getRevenue,
        getProductRotationUseCase = getRotation,
        getMarginsUseCase = getMargins,
        scope = CoroutineScope(UnconfinedTestDispatcher())
    )

    @Test
    fun initialState_loadsAllData() {
        val revenue = RevenueSummary(500.0, 10, 50.0, emptyList())
        val rotation = listOf(ProductRotationEntry(1L, "A", "Cat", 20, 200.0))
        val margins = listOf(MarginEntry(1L, "A", "Cat", 50.0, 100.0, 50.0, 50.0))

        repo.revenueSummaryResult = revenue
        repo.productRotationResult = rotation
        repo.marginsResult = margins

        val vm = buildViewModel()
        val state = vm.uiState.value

        assertEquals(revenue, state.revenueSummary)
        assertEquals(rotation, state.rotation)
        assertEquals(margins, state.margins)
        assertFalse(state.isLoadingRevenue)
        assertFalse(state.isLoadingRotation)
        assertFalse(state.isLoadingMargins)
        assertNull(state.error)
    }

    @Test
    fun onFiltersChanged_reloadsRevenueAndRotationButNotMargins() {
        val initialMargins = listOf(MarginEntry(1L, "A", "Cat", 50.0, 100.0, 50.0, 50.0))
        repo.marginsResult = initialMargins
        val vm = buildViewModel()

        val newRevenue = RevenueSummary(999.0, 1, 999.0, emptyList())
        val newRotation = listOf(ProductRotationEntry(2L, "B", "Cat2", 5, 50.0))
        repo.revenueSummaryResult = newRevenue
        repo.productRotationResult = newRotation

        val newFilters = ReportFilters.default().copy(period = ReportPeriod.DAILY)
        vm.onFiltersChanged(newFilters)

        val state = vm.uiState.value
        assertEquals(newRevenue, state.revenueSummary)
        assertEquals(newRotation, state.rotation)
        assertEquals(initialMargins, state.margins)
        assertEquals(newFilters, state.filters)
    }

    @Test
    fun error_setsErrorState() {
        repo.shouldThrowError = true

        val vm = buildViewModel()

        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoadingRevenue)
        assertFalse(vm.uiState.value.isLoadingRotation)
    }
}
