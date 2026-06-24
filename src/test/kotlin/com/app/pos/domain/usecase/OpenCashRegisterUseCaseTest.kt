package com.app.pos.domain.usecase

import com.app.pos.domain.entity.SessionStatus
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCaseImpl
import com.app.pos.domain.usecase.cashregister.OpenCashRegisterUseCaseImpl
import com.app.pos.data.repository.CashRegisterRepositoryFake
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OpenCashRegisterUseCaseTest {

    private val repo = CashRegisterRepositoryFake()
    private val getActive = GetActiveSessionUseCaseImpl(repo)
    private val useCase = OpenCashRegisterUseCaseImpl(getActive, repo)

    @Test
    fun noActiveSession_sessionCreatedAndReturned() = runBlocking {
        val result = useCase(100.0)

        assertTrue(result.isSuccess)
        val session = result.getOrNull()
        assertNotNull(session)
        assertEquals(session.status, SessionStatus.OPEN)
    }

    @Test
    fun activeSessionExists_returnsFailure() = runBlocking {
        useCase(100.0)

        val result = useCase(200.0)

        assertTrue(result.isFailure)
    }
}