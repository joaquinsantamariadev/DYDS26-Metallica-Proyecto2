package com.app.settings.presentation

import com.app.settings.data.repository.ExportRepositoryFake
import com.app.settings.data.repository.SettingsRepositoryFake
import com.app.settings.domain.entity.ExportFormat
import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.usecase.ExportDataUseCaseImpl
import com.app.settings.domain.usecase.GetStoreSettingsUseCaseImpl
import com.app.settings.domain.usecase.GetSystemSettingsUseCaseImpl
import com.app.settings.domain.usecase.SaveStoreSettingsUseCaseImpl
import com.app.settings.domain.usecase.SaveSystemSettingsUseCaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private fun buildViewModel(
        settingsRepository: SettingsRepositoryFake,
        exportRepository: ExportRepositoryFake
    ): SettingsViewModel {
        return SettingsViewModel(
            getStoreSettingsUseCase = GetStoreSettingsUseCaseImpl(settingsRepository),
            getSystemSettingsUseCase = GetSystemSettingsUseCaseImpl(settingsRepository),
            saveStoreSettingsUseCase = SaveStoreSettingsUseCaseImpl(settingsRepository),
            saveSystemSettingsUseCase = SaveSystemSettingsUseCaseImpl(settingsRepository),
            exportDataUseCase = ExportDataUseCaseImpl(exportRepository),
            scope = CoroutineScope(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun init_loadsSettingsIntoUiState() {
        val settingsRepository = SettingsRepositoryFake().apply {
            storeSettingsResult = StoreSettings(storeName = "Mi Local", currency = "ARS")
            systemSettingsResult = SystemSettings(defaultLowStockThreshold = 9, expiryAlertDays = 11)
        }
        val exportRepository = ExportRepositoryFake()

        val viewModel = buildViewModel(settingsRepository, exportRepository)
        val state = viewModel.uiState.value

        assertEquals("Mi Local", state.storeSettings.storeName)
        assertEquals(9, state.systemSettings.defaultLowStockThreshold)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun init_setsErrorWhenRepositoryThrows() {
        val settingsRepository = SettingsRepositoryFake().apply {
            shouldThrowOnGetStore = true
        }

        val viewModel = buildViewModel(settingsRepository, ExportRepositoryFake())
        val state = viewModel.uiState.value

        assertNotNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun saveStoreSettings_updatesStateWithSuccess() {
        val settingsRepository = SettingsRepositoryFake()
        val viewModel = buildViewModel(settingsRepository, ExportRepositoryFake())
        val input = StoreSettings(storeName = "Sucursal Centro")

        viewModel.saveStoreSettings(input)
        val state = viewModel.uiState.value

        assertTrue(settingsRepository.saveStoreCalled)
        assertTrue(state.saveSuccess)
        assertFalse(state.isSaving)
        assertEquals(input, state.storeSettings)
    }

    @Test
    fun saveSystemSettings_updatesStateWithSuccess() {
        val settingsRepository = SettingsRepositoryFake()
        val viewModel = buildViewModel(settingsRepository, ExportRepositoryFake())
        val input = SystemSettings(defaultLowStockThreshold = 12, expiryAlertDays = 14)

        viewModel.saveSystemSettings(input)
        val state = viewModel.uiState.value

        assertTrue(settingsRepository.saveSystemCalled)
        assertTrue(state.saveSuccess)
        assertFalse(state.isSaving)
        assertEquals(input, state.systemSettings)
    }

    @Test
    fun saveStoreSettings_setsErrorWhenUseCaseFails() {
        val settingsRepository = SettingsRepositoryFake().apply {
            shouldThrowOnSaveStore = true
        }
        val viewModel = buildViewModel(settingsRepository, ExportRepositoryFake())

        viewModel.saveStoreSettings(StoreSettings(storeName = "Sucursal Centro"))

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.saveSuccess)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun exportData_setsSuccessStatus() {
        val exportRepository = ExportRepositoryFake()
        val viewModel = buildViewModel(SettingsRepositoryFake(), exportRepository)

        viewModel.exportData("C:\\exports\\backup", ExportFormat.CSV)

        assertEquals("Éxito", viewModel.uiState.value.exportStatus)
        assertTrue(exportRepository.exportProductsCalled)
        assertTrue(exportRepository.exportSalesCalled)
        assertTrue(exportRepository.exportCategoriesCalled)
    }

    @Test
    fun exportData_setsErrorStatusWhenExportFails() {
        val exportRepository = ExportRepositoryFake().apply {
            shouldThrowOnSales = true
        }
        val viewModel = buildViewModel(SettingsRepositoryFake(), exportRepository)

        viewModel.exportData("C:\\exports\\backup", ExportFormat.CSV)

        assertNotNull(viewModel.uiState.value.exportStatus)
        assertTrue(viewModel.uiState.value.exportStatus!!.startsWith("Error:"))
    }
}
