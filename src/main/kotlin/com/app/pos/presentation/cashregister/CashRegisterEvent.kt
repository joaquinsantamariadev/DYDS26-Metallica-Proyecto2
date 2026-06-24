package com.app.pos.presentation.cashregister

sealed class CashRegisterEvent {
    data class OpenSession(val openingAmount: Double) : CashRegisterEvent()
    data class CloseSession(val closingAmount: Double) : CashRegisterEvent()
    object LoadHistory : CashRegisterEvent()
    object DismissError : CashRegisterEvent()
}