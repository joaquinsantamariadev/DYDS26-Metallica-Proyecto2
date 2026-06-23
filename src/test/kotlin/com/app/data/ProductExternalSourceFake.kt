package com.app.data

import com.app.domain.entity.Product
import com.app.domain.repository.ProductExternalSource

class ProductExternalSourceFake : ProductExternalSource {
    var result: Product? = null
    var shouldThrowError = false

    override suspend fun fetchByBarcode(barcode: String): Product? {
        if (shouldThrowError) throw Exception("Network Error")
        return result
    }
}
