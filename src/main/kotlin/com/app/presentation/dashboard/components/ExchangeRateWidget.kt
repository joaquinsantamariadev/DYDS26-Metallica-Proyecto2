package com.app.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.ExchangeRate

@Composable
fun ExchangeRateWidget(
    exchangeRate: ExchangeRate?,
    unavailable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Dólar Blue",
                style = MaterialTheme.typography.subtitle1
            )
            when {
                unavailable -> Text(
                    text = "Sin conexión y sin datos previos",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error
                )
                exchangeRate != null -> Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ %.2f".format(exchangeRate.rate),
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = exchangeRate.currencyPair,
                        style = MaterialTheme.typography.caption
                    )
                }
                else -> Text(
                    text = "Cargando...",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}