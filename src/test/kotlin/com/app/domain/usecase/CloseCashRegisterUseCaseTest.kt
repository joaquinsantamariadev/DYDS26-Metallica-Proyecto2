package com.app.domain.usecase

import com.app.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.cashregister.OpenCashRegisterUseCase
import com.app.fakes.CashRegisterRepositoryFake
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class CloseCashRegisterUseCaseTest {

    private val repo = CashRegisterRepositoryFake()
    private val getActive = GetActiveSessionUseCase(repo)
    private val openUseCase = OpenCashRegisterUseCase(getActive, repo)
    private val useCase = CloseCashRegisterUseCase(getActive, repo)

    @Test
    fun activeSession_sessionClosedAndFlagSet() = runBlocking {
        openUseCase(100.0)

        val result = useCase(250.0)

        assertTrue(result.isSuccess)
        assertTrue(repo.closeCalled)
    }

    @Test
    fun noActiveSession_returnsFailure() = runBlocking {
        val result = useCase(250.0)

        assertTrue(result.isFailure)
    }
}