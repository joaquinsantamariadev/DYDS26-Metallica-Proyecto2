package com.app.inventory.data.repository

import com.app.inventory.domain.entity.Product
import com.app.inventory.domain.repository.ProductExternalSource

class ProductExternalSourceFake : ProductExternalSource {
    var result: Product? = null
    var shouldThrowError = false

    override suspend fun fetchByBarcode(barcode: String): Product? {
        if (shouldThrowError) throw Exception("Network Error")
        return result
    }
}
