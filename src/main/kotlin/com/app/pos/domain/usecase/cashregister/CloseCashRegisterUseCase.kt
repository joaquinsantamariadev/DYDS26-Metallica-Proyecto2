package com.app.pos.domain.usecase.cashregister

import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.repository.CashRegisterRepository

interface CloseCashRegisterUseCase {
    suspend operator fun invoke(closingAmount: Double): Result<CashRegisterSession>
}

class CloseCashRegisterUseCaseImpl(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val cashRegisterRepository: CashRegisterRepository
) : CloseCashRegisterUseCase {
    override suspend operator fun invoke(closingAmount: Double): Result<CashRegisterSession> {
        val active = getActiveSessionUseCase()
            ?: return Result.failure(IllegalStateException("No hay sesión activa"))
        return runCatching { cashRegisterRepository.close(active.id!!, closingAmount) }
    }
}