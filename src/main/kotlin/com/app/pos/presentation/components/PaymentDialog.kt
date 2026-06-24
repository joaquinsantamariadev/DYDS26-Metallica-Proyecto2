package com.app.pos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.pos.domain.entity.PaymentMethod
import com.app.common.presentation.utils.BoneWhite
import com.app.common.presentation.utils.CharcoalBrown
import com.app.common.presentation.utils.PeachOrange
import com.app.common.presentation.utils.TaupeGray

@Composable
fun PaymentDialog(onConfirm: (PaymentMethod) -> Unit, onDismiss: () -> Unit) {
    var selected by remember { mutableStateOf(PaymentMethod.CASH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Método de pago", color = CharcoalBrown, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column {
                PaymentMethod.entries.forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selected == method,
                            onClick = { selected = method },
                            colors = RadioButtonDefaults.colors(selectedColor = PeachOrange)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(method.label(), color = CharcoalBrown, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selected) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = PeachOrange, contentColor = BoneWhite)
            ) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TaupeGray) }
        },
        backgroundColor = BoneWhite,
        shape = RoundedCornerShape(16.dp)
    )
}

private fun PaymentMethod.label() = when (this) {
    PaymentMethod.CASH -> "Efectivo"
    PaymentMethod.CARD -> "Tarjeta"
    PaymentMethod.TRANSFER -> "Transferencia"
}