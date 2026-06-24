package com.app.pos.presentation.cashregister

import com.app.pos.domain.usecase.cashregister.CloseCashRegisterUseCaseImpl
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCaseImpl
import com.app.pos.domain.usecase.cashregister.OpenCashRegisterUseCaseImpl
import com.app.pos.data.repository.CashRegisterRepositoryFake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CashRegisterViewModelTest {

    private val repo = CashRegisterRepositoryFake()
    private val getActive = GetActiveSessionUseCaseImpl(repo)
    private val openUseCase = OpenCashRegisterUseCaseImpl(getActive, repo)
    private val closeUseCase = CloseCashRegisterUseCaseImpl(getActive, repo)

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