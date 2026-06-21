package com.app.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun KpiCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Card(modifier = modifier.width(180.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(text = title, style = MaterialTheme.typography.caption)
            Text(text = value, style = MaterialTheme.typography.h6)
        }
    }
}