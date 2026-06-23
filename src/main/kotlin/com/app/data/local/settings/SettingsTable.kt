package com.app.data.local.settings

import org.jetbrains.exposed.sql.Table

object SettingsTable : Table("settings") {
    val key = varchar("key", 100)
    val value = text("value")

    override val primaryKey = PrimaryKey(key)
}
