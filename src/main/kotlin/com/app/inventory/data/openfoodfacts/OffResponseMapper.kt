package com.app.inventory.data.openfoodfacts

import com.app.inventory.data.openfoodfacts.dto.OffResponse
import com.app.inventory.domain.entity.Product

fun OffResponse.toProduct(barcode: String): Product? {
    if (this.status == 1 && this.product != null) {
        return Product(
            barcode = barcode,
            name = this.product.productName ?: "Producto Sin Nombre",
            categoryId = null,
            price = 0.0,
            cost = 0.0,
            stock = 0
        )
    }
    return null
}
