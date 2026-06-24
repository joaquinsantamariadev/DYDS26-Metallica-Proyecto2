package com.app.inventory.domain.repository

import com.app.inventory.domain.entity.Product

interface ProductExternalSource {
    suspend fun fetchByBarcode(barcode: String): Product?
}