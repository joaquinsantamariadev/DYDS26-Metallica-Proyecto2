package com.app.domain.usecase.cashregister

import com.app.domain.entity.CashRegisterSession
import com.app.domain.repository.CashRegisterRepository

class OpenCashRegisterUseCase(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val cashRegisterRepository: CashRegisterRepository
) {
    suspend operator fun invoke(openingAmount: Double): Result<CashRegisterSession> {
        if (getActiveSessionUseCase() != null)
            return Result.failure(IllegalStateException("Ya existe una sesión abierta"))
        return runCatching { cashRegisterRepository.open(openingAmount) }
    }
}