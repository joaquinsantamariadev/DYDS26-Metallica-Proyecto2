package com.app.settings.data.repository

import com.app.settings.data.local.SettingsTable
import com.app.common.data.mapper.toSettingsMap
import com.app.common.data.mapper.toStoreSettings
import com.app.common.data.mapper.toSystemSettings
import com.app.common.data.mapper.toKeyValuePairs
import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.repository.SettingsRepository
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
