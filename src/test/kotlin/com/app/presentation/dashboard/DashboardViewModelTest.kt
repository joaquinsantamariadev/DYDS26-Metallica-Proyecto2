package com.app.presentation.dashboard

import com.app.data.DashboardRepositoryFake
import com.app.data.ExchangeRateRepositoryFake
import com.app.domain.entity.ExchangeRate
import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.usecase.dashboard.GetDashboardMetricsUseCase
import com.app.domain.usecase.dashboard.GetExpiryAlertsUseCase
import com.app.domain.usecase.dashboard.GetLowStockAlertsUseCase
import com.app.domain.usecase.dashboard.GetRecentSalesUseCase
import com.app.domain.usecase.exchangerate.GetExchangeRateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var dashboardRepository: DashboardRepositoryFake
    private lateinit var exchangeRateRepository: ExchangeRateRepositoryFake

    @Before
    fun setUp() {
        dashboardRepository = DashboardRepositoryFake()
        exchangeRateRepository = ExchangeRateRepositoryFake()
    }

    private fun buildViewModel() = DashboardViewModel(
        getMetrics = GetDashboardMetricsUseCase(dashboardRepository),
        getLowStockAlerts = GetLowStockAlertsUseCase(dashboardRepository),
        getExpiryAlerts = GetExpiryAlertsUseCase(dashboardRepository),
        getRecentSales = GetRecentSalesUseCase(dashboardRepository),
        getExchangeRateUseCase = GetExchangeRateUseCase(exchangeRateRepository),
        scope = kotlinx.coroutines.CoroutineScope(UnconfinedTestDispatcher())
    )

    @Test
    fun loadsMetricsOnInit() {
        runTest {
            val expected = DashboardMetrics(10, 500.0, 3, 150.0, 2, 1, true)
            dashboardRepository.metricsResult = expected

            val viewModel = buildViewModel()

            assertEquals(expected, viewModel.uiState.value.metrics)
        }
    }

    @Test
    fun loadsExchangeRateOnInit() {
        runTest {
            val expected = ExchangeRate(currencyPair = "USD/ARS", rate = 1020.0, lastUpdated = 0L)
            exchangeRateRepository.blueRate = expected

            val viewModel = buildViewModel()

            assertEquals(expected, viewModel.uiState.value.exchangeRate)
            assertEquals(false, viewModel.uiState.value.exchangeRateUnavailable)
        }
    }

    @Test
    fun setsUnavailableWhenExchangeRateFailsAndNoCacheExists() {
        runTest {
            exchangeRateRepository.shouldThrowError = true
            exchangeRateRepository.cachedRate = null

            val viewModel = buildViewModel()

            assertEquals(null, viewModel.uiState.value.exchangeRate)
            assertEquals(true, viewModel.uiState.value.exchangeRateUnavailable)
        }
    }

    @Test
    fun exchangeRateErrorDoesNotAffectMetrics() {
        runTest {
            val expected = DashboardMetrics(5, 200.0, 1, 50.0, 0, 0, false)
            dashboardRepository.metricsResult = expected
            exchangeRateRepository.shouldThrowError = true
            exchangeRateRepository.cachedRate = null

            val viewModel = buildViewModel()

            assertEquals(expected, viewModel.uiState.value.metrics)
            assertEquals(true, viewModel.uiState.value.exchangeRateUnavailable)
        }
    }

    @Test
    fun setsErrorWhenDashboardRepositoryThrows() {
        runTest {
            dashboardRepository.shouldThrowError = true

            val viewModel = buildViewModel()

            assertEquals(true, viewModel.uiState.value.error != null)
            assertEquals(false, viewModel.uiState.value.isLoading)
        }
    }

    @Test
    fun refreshReloadsAllData() {
        runTest {
            val viewModel = buildViewModel()
            val updated = DashboardMetrics(99, 9999.0, 5, 500.0, 3, 2, true)
            dashboardRepository.metricsResult = updated

            viewModel.refresh()

            assertEquals(updated, viewModel.uiState.value.metrics)
        }
    }
}