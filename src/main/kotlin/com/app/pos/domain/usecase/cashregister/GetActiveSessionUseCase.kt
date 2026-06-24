package com.app.pos.domain.usecase.cashregister

import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.repository.CashRegisterRepository

interface GetActiveSessionUseCase {
    suspend operator fun invoke(): CashRegisterSession?
}

class GetActiveSessionUseCaseImpl(
    private val cashRegisterRepository: CashRegisterRepository
) : GetActiveSessionUseCase {
    override suspend operator fun invoke(): CashRegisterSession? =
        cashRegisterRepository.getActive()
}