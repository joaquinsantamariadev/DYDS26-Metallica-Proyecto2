package com.app.data

import com.app.domain.entity.Category
import com.app.domain.repository.CategoryRepository

class CategoryRepositoryFake : CategoryRepository {
    val categoriesList = mutableListOf<Category>()
    var insertCalled = false
    var updateCalled = false
    var deleteCalled = false

    override suspend fun getCategories(): List<Category> = categoriesList

    override suspend fun insertCategory(category: Category) {
        insertCalled = true
        categoriesList.add(category.copy(id = categoriesList.size + 1))
    }

    override suspend fun updateCategory(category: Category) {
        updateCalled = true
        val index = categoriesList.indexOfFirst { it.id == category.id }
        if (index >= 0) categoriesList[index] = category
    }

    override suspend fun deleteCategory(id: Int) {
        deleteCalled = true
        categoriesList.removeAll { it.id == id }
    }
}
