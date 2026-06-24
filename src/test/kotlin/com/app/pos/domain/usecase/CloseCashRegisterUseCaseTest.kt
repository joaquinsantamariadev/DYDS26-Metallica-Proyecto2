package com.app.pos.domain.usecase

import com.app.pos.domain.usecase.cashregister.CloseCashRegisterUseCaseImpl
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCaseImpl
import com.app.pos.domain.usecase.cashregister.OpenCashRegisterUseCaseImpl
import com.app.pos.data.repository.CashRegisterRepositoryFake
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class CloseCashRegisterUseCaseTest {

    private val repo = CashRegisterRepositoryFake()
    private val getActive = GetActiveSessionUseCaseImpl(repo)
    private val openUseCase = OpenCashRegisterUseCaseImpl(getActive, repo)
    private val useCase = CloseCashRegisterUseCaseImpl(getActive, repo)

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