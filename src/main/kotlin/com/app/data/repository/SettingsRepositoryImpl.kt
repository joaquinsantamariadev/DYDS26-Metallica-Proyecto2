package com.app.data.repository

import com.app.data.local.settings.SettingsTable
import com.app.data.mapper.toSettingsMap
import com.app.data.mapper.toStoreSettings
import com.app.data.mapper.toSystemSettings
import com.app.data.mapper.toKeyValuePairs
import com.app.domain.entity.settings.StoreSettings
import com.app.domain.entity.settings.SystemSettings
import com.app.domain.repository.SettingsRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SettingsRepositoryImpl : SettingsRepository {
    override suspend fun getStoreSettings(): StoreSettings = transaction {
        SettingsTable.selectAll().toSettingsMap().toStoreSettings()
    }

    override suspend fun saveStoreSettings(settings: StoreSettings) = transaction {
        settings.toKeyValuePairs().forEach { (key, value) ->
            val updated = SettingsTable.update({ SettingsTable.key eq key }) {
                it[SettingsTable.value] = value
            }
            if (updated == 0) {
                SettingsTable.insert {
                    it[SettingsTable.key] = key
                    it[SettingsTable.value] = value
                }
            }
        }
    }

    override suspend fun getSystemSettings(): SystemSettings = transaction {
        SettingsTable.selectAll().toSettingsMap().toSystemSettings()
    }

    override suspend fun saveSystemSettings(settings: SystemSettings) = transaction {
        settings.toKeyValuePairs().forEach { (key, value) ->
            val updated = SettingsTable.update({ SettingsTable.key eq key }) {
                it[SettingsTable.value] = value
            }
            if (updated == 0) {
                SettingsTable.insert {
                    it[SettingsTable.key] = key
                    it[SettingsTable.value] = value
                }
            }
        }
    }
}
