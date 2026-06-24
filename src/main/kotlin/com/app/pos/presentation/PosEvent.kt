package com.app.pos.presentation

import com.app.pos.domain.entity.PaymentMethod
import com.app.inventory.domain.entity.Product

sealed class PosEvent {
    data class AddItem(val product: Product, val quantity: Int) : PosEvent()
    data class RemoveItem(val productId: Int) : PosEvent()
    data class UpdateQuantity(val productId: Int, val quantity: Int) : PosEvent()
    data class CompleteSale(val paymentMethod: PaymentMethod) : PosEvent()
    data class SearchByName(val query: String) : PosEvent()
    data class ScanBarcode(val barcode: String) : PosEvent()
    object ClearCart : PosEvent()
    object DismissError : PosEvent()
    object AcknowledgeSale : PosEvent()
    object RefreshSession : PosEvent()
}