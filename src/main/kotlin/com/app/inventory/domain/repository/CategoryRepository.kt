package com.app.inventory.domain.repository

import com.app.inventory.domain.entity.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(id: Int)
}