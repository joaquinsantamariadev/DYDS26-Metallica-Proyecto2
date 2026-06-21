package com.app.presentation.pos.cashregister

import com.app.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.cashregister.OpenCashRegisterUseCase
import com.app.fakes.CashRegisterRepositoryFake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CashRegisterViewModelTest {

    private val repo = CashRegisterRepositoryFake()
    private val getActive = GetActiveSessionUseCase(repo)
    private val openUseCase = OpenCashRegisterUseCase(getActive, repo)
    private val closeUseCase = CloseCashRegisterUseCase(getActive, repo)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildViewModel() = CashRegisterViewModel(
        openCashRegisterUseCase = openUseCase,
        closeCashRegisterUseCase = closeUseCase,
        getActiveSessionUseCase = getActive,
        cashRegisterRepository = repo,
        scope = CoroutineScope(UnconfinedTestDispatcher())
    )

    @Test
    fun openSession_noExistingSession_activeSessionSetInState() {
        val vm = buildViewModel()

        vm.onEvent(CashRegisterEvent.OpenSession(100.0))

        assertNotNull(vm.state.value.activeSession)
    }

    @Test
    fun openSession_withExistingSession_errorPropagated() {
        val vm = buildViewModel()
        vm.onEvent(CashRegisterEvent.OpenSession(100.0))

        vm.onEvent(CashRegisterEvent.OpenSession(200.0))

        assertNotNull(vm.state.value.error)
    }

    @Test
    fun closeSession_withActiveSession_activeSessionNullInState() {
        val vm = buildViewModel()
        vm.onEvent(CashRegisterEvent.OpenSession(100.0))

        vm.onEvent(CashRegisterEvent.CloseSession(250.0))

        assertNull(vm.state.value.activeSession)
        assertTrue(repo.closeCalled)
    }

    @Test
    fun closeSession_withoutActiveSession_errorPropagated() {
        val vm = buildViewModel()

        vm.onEvent(CashRegisterEvent.CloseSession(250.0))

        assertNotNull(vm.state.value.error)
    }
}