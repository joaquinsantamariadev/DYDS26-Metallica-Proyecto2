package com.app.pos.domain.usecase.cashregister

import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.repository.CashRegisterRepository

interface OpenCashRegisterUseCase {
    suspend operator fun invoke(openingAmount: Double): Result<CashRegisterSession>
}

class OpenCashRegisterUseCaseImpl(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val cashRegisterRepository: CashRegisterRepository
) : OpenCashRegisterUseCase {
    override suspend operator fun invoke(openingAmount: Double): Result<CashRegisterSession> {
        if (getActiveSessionUseCase() != null)
            return Result.failure(IllegalStateException("Ya existe una sesión abierta"))
        return runCatching { cashRegisterRepository.open(openingAmount) }
    }
}