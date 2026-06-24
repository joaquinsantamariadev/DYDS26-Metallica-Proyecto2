package com.app.reports.domain.entity

import java.time.LocalDate

data class ReportFilters(
    val from: LocalDate,
    val to: LocalDate,
    val period: ReportPeriod = ReportPeriod.MONTHLY
) {
    companion object {
        fun default(): ReportFilters {
            val today = LocalDate.now()
            return ReportFilters(
                from = today.withDayOfMonth(1).minusMonths(5),
                to = today
            )
        }
    }
}