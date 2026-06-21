package com.app.data.repository

import com.app.data.local.inventory.CategoryTable
import com.app.data.mapper.toCategory
import com.app.domain.entity.Category
import com.app.domain.repository.CategoryRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class CategoryRepositoryImpl : CategoryRepository {
    override suspend fun getCategories(): List<Category> = transaction {
        CategoryTable.selectAll().map { it.toCategory() }
    }

    override suspend fun insertCategory(category: Category) {
        transaction {
            CategoryTable.insert {
                it[name] = category.name
            }
        }
    }

    override suspend fun updateCategory(category: Category) {
        transaction {
            CategoryTable.update({ CategoryTable.id eq category.id!! }) {
                it[name] = category.name
            }
        }
    }

    override suspend fun deleteCategory(id: Int) {
        transaction {
            CategoryTable.deleteWhere { CategoryTable.id eq id }
        }
    }
}