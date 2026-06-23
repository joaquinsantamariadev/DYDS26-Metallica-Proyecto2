package com.app.domain.usecase.exchangerate

import com.app.domain.entity.ExchangeRate
import com.app.domain.repository.ExchangeRateRepository

class GetExchangeRateUseCase(private val repository: ExchangeRateRepository) {
    suspend operator fun invoke(): ExchangeRate = repository.getBlueRate()
}