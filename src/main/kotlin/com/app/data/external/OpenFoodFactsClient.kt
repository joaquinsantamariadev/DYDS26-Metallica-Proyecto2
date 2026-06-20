package com.app.data.external

import com.app.data.external.dto.OffResponse
import com.app.domain.repository.ProductExternalSource
import com.app.domain.entity.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OpenFoodFactsClient(private val client: HttpClient) : ProductExternalSource {
    override suspend fun fetchByBarcode(barcode: String): Product? {
        return try {
            val response: OffResponse =
                client.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json").body()
            if (response.status == 1 && response.product != null) {
                Product(
                    barcode = barcode,
                    name = response.product.productName ?: "Producto Sin Nombre",
                    categoryId = null,
                    price = 0.0,
                    cost = 0.0,
                    stock = 0
                )
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}