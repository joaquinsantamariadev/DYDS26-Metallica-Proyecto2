package com.app.data.repository

import com.app.data.local.settings.SettingsTable
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
        val settings = SettingsTable.selectAll().associate {
            it[SettingsTable.key] to it[SettingsTable.value]
        }
        StoreSettings(
            storeName = settings["store_name"] ?: "",
            address = settings["address"] ?: "",
            phone = settings["phone"] ?: "",
            currency = settings["currency"] ?: "ARS",
            logoPath = settings["logo_path"] ?: ""
        )
    }

    override suspend fun saveStoreSettings(settings: StoreSettings) = transaction {
        val data = mapOf(
            "store_name" to settings.storeName,
            "address" to settings.address,
            "phone" to settings.phone,
            "currency" to settings.currency,
            "logo_path" to settings.logoPath
        )

        data.forEach { (key, value) ->
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
        val settings = SettingsTable.selectAll().associate {
            it[SettingsTable.key] to it[SettingsTable.value]
        }
        SystemSettings(
            defaultLowStockThreshold = settings["default_low_stock_threshold"]?.toIntOrNull() ?: 5,
            expiryAlertDays = settings["expiry_alert_days"]?.toIntOrNull() ?: 7,
            historyPageSize = settings["history_page_size"]?.toIntOrNull() ?: 50,
            rotationTopN = settings["rotation_top_n"]?.toIntOrNull() ?: 20
        )
    }

    override suspend fun saveSystemSettings(settings: SystemSettings) = transaction {
        val data = mapOf(
            "default_low_stock_threshold" to settings.defaultLowStockThreshold.toString(),
            "expiry_alert_days" to settings.expiryAlertDays.toString(),
            "history_page_size" to settings.historyPageSize.toString(),
            "rotation_top_n" to settings.rotationTopN.toString()
        )

        data.forEach { (key, value) ->
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
