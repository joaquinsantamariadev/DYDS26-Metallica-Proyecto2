package com.app.data.local

import org.jetbrains.exposed.sql.Table

object CategoryTable : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}
