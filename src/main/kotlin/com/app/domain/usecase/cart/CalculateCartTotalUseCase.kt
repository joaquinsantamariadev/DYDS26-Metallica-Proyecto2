package com.app.domain.usecase.cart

class CalculateCartTotalUseCase {
    operator fun invoke(items: List<Pair<Double, Int>>): Double =
        items.sumOf { (price, qty) -> price * qty }
}