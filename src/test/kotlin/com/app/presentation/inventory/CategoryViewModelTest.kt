package com.app.presentation.inventory

import com.app.data.FakeCategoryRepository
import com.app.domain.entity.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoryViewModelTest {

    private val repo = FakeCategoryRepository()
    private val viewModel = CategoryViewModel(repo)

    @Test
    fun testAddCategory() = runBlocking {
        viewModel.addCategory("Lácteos")
        delay(100)
        assertTrue(repo.insertCalled)
        assertEquals(1, repo.categoriesList.size)
        assertEquals("Lácteos", repo.categoriesList[0].name)
    }

    @Test
    fun testDeleteCategory() = runBlocking {
        repo.categoriesList.add(Category(id = 1, name = "Lácteos"))
        viewModel.deleteCategory(1)
        delay(100)
        assertTrue(repo.deleteCalled)
        assertTrue(repo.categoriesList.isEmpty())
    }

    @Test
    fun testUpdateCategory() = runBlocking {
        repo.categoriesList.add(Category(id = 1, name = "Lácteos"))
        viewModel.updateCategory(Category(id = 1, name = "Bebidas"))
        delay(100)
        assertTrue(repo.updateCalled)
        assertEquals("Bebidas", repo.categoriesList[0].name)
    }
}