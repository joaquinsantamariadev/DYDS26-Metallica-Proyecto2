package com.app.exchangerate.domain.usecase

import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.exchangerate.domain.repository.ExchangeRateRepository

interface GetExchangeRateUseCase {
    suspend operator fun invoke(): ExchangeRate
}

class GetExchangeRateUseCaseImpl(private val repository: ExchangeRateRepository) : GetExchangeRateUseCase {
    override suspend operator fun invoke(): ExchangeRate = repository.getBlueRate()
}