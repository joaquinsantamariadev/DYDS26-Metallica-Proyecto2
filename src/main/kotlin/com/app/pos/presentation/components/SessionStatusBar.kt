package com.app.pos.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.pos.domain.entity.CashRegisterSession
import com.app.common.presentation.utils.*
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun SessionStatusBar(activeSession: CashRegisterSession?, onManageSession: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(BoneWhite)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (activeSession != null) Icons.Default.LockOpen else Icons.Default.Lock,
                null,
                tint = if (activeSession != null) PeachOrange else TaupeGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (activeSession != null)
                    "Caja abierta desde ${activeSession.openedAt.format(timeFormatter)}"
                else
                    "Sin sesión de caja activa",
                color = if (activeSession != null) CharcoalBrown else TaupeGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Button(
            onClick = onManageSession,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (activeSession != null) DarkSand else PeachOrange,
                contentColor = if (activeSession != null) CharcoalBrown else BoneWhite
            ),
            modifier = Modifier
        ) {
            Text(if (activeSession != null) "Gestionar caja" else "Abrir caja", fontSize = 12.sp)
        }
    }
}