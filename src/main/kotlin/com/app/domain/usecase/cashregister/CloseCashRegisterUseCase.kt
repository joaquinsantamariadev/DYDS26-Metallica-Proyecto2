package com.app.domain.usecase.cashregister

import com.app.domain.entity.CashRegisterSession
import com.app.domain.repository.CashRegisterRepository

class CloseCashRegisterUseCase(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val cashRegisterRepository: CashRegisterRepository
) {
    suspend operator fun invoke(closingAmount: Double): Result<CashRegisterSession> {
        val active = getActiveSessionUseCase()
            ?: return Result.failure(IllegalStateException("No hay sesión activa"))
        return runCatching { cashRegisterRepository.close(active.id!!, closingAmount) }
    }
}