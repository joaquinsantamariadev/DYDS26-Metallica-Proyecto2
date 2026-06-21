package com.app.presentation.pos.cashregister

sealed class CashRegisterEvent {
    data class OpenSession(val openingAmount: Double) : CashRegisterEvent()
    data class CloseSession(val closingAmount: Double) : CashRegisterEvent()
    object LoadHistory : CashRegisterEvent()
    object DismissError : CashRegisterEvent()
}