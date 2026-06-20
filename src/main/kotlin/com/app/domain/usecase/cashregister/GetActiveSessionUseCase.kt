package com.app.domain.usecase.cashregister

import com.app.domain.entity.CashRegisterSession
import com.app.domain.repository.CashRegisterRepository

class GetActiveSessionUseCase(
    private val cashRegisterRepository: CashRegisterRepository
) {
    suspend operator fun invoke(): CashRegisterSession? =
        cashRegisterRepository.getActive()
}