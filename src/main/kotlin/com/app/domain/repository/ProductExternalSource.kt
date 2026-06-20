package com.app.domain.repository

import com.app.domain.entity.Product

interface ProductExternalSource {
    suspend fun fetchByBarcode(barcode: String): Product?
}