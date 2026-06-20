package com.app.data.mapper

import com.app.data.local.CategoryTable
import com.app.data.local.ExchangeRateTable
import com.app.data.local.ProductTable
import com.app.domain.entity.Category
import com.app.domain.entity.ExchangeRate
import com.app.domain.entity.Product
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toCategory() = Category(
    id = this[CategoryTable.id],
    name = this[CategoryTable.name]
)

fun ResultRow.toProduct() = Product(
    id = this[ProductTable.id],
    barcode = this[ProductTable.barcode],
    name = this[ProductTable.name],
    categoryId = this[ProductTable.categoryId],
    price = this[ProductTable.price],
    cost = this[ProductTable.cost],
    stock = this[ProductTable.stock],
    imageUrl = this[ProductTable.imageUrl],
    expiryDate = this[ProductTable.expiryDate]
)

fun ResultRow.toExchangeRate() = ExchangeRate(
    currencyPair = this[ExchangeRateTable.currencyPair],
    rate = this[ExchangeRateTable.rate],
    lastUpdated = this[ExchangeRateTable.lastUpdated]
)
