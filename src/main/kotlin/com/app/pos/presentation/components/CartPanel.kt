package com.app.pos.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.pos.presentation.CartItem
import com.app.pos.presentation.PosEvent
import com.app.pos.presentation.PosState
import com.app.common.presentation.utils.*

@Composable
fun CartPanel(
    state: PosState,
    onEvent: (PosEvent) -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxHeight().padding(20.dp)) {
        Text("Carrito", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onSurface)
        Spacer(Modifier.height(16.dp))

        if (state.cartItems.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("El carrito está vacío", color = TaupeGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.cartItems, key = { it.product.id ?: it.product.name }) { item ->
                    CartItemRow(item = item, onEvent = onEvent)
                    Divider(color = DarkSand.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Divider(color = DarkSand)
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onSurface)
            Text(
                "$${"%.2f".format(state.cartTotal)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PeachOrange
            )
        }

        Spacer(Modifier.height(16.dp))

        if (state.activeSession == null) {
            Text(
                "Abrí una sesión de caja para cobrar",
                color = TaupeGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = onCheckout,
            enabled = state.activeSession != null && state.cartItems.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = PeachOrange,
                contentColor = MaterialTheme.colors.surface,
                disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
            ),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Cobrar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onEvent: (PosEvent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, fontWeight = FontWeight.Medium, color = MaterialTheme.colors.onSurface, fontSize = 14.sp)
            Text("$${"%.2f".format(item.product.price)} c/u", fontSize = 12.sp, color = TaupeGray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onEvent(PosEvent.UpdateQuantity(item.product.id!!, item.quantity - 1)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, null, tint = TaupeGray, modifier = Modifier.size(16.dp))
            }
            Text(
                item.quantity.toString(),
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 28.dp)
            )
            IconButton(
                onClick = { onEvent(PosEvent.UpdateQuantity(item.product.id!!, item.quantity + 1)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = TaupeGray, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "$${"%.2f".format(item.subtotal)}",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.widthIn(min = 72.dp),
            textAlign = TextAlign.End
        )
        IconButton(
            onClick = { onEvent(PosEvent.RemoveItem(item.product.id!!)) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Delete, null, tint = PeachOrange, modifier = Modifier.size(16.dp))
        }
    }
}