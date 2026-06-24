package com.app.inventory.data.openfoodfacts

import com.app.inventory.data.openfoodfacts.dto.OffResponse
import com.app.inventory.domain.entity.Product
import com.app.inventory.domain.repository.ProductExternalSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OpenFoodFactsClient(private val client: HttpClient) : ProductExternalSource {
    override suspend fun fetchByBarcode(barcode: String): Product? {
        return try {
            val response: OffResponse =
                client.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
            response.toProduct(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}