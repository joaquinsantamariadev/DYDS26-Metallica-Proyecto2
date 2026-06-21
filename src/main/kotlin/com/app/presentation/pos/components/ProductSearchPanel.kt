package com.app.presentation.pos.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.domain.entity.Product
import com.app.presentation.pos.PosEvent
import com.app.presentation.pos.PosState
import com.app.presentation.utils.*

@Composable
fun ProductSearchPanel(state: PosState, onEvent: (PosEvent) -> Unit, modifier: Modifier = Modifier) {
    var barcodeInput by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxHeight().padding(20.dp)) {
        Text("Buscar producto", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CharcoalBrown)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = barcodeInput,
            onValueChange = { barcodeInput = it },
            label = { Text("Código de barras", color = TaupeGray) },
            leadingIcon = { Icon(Icons.Default.QrCode, null, tint = TaupeGray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = posTextFieldColors(),
            modifier = Modifier.fillMaxWidth().onKeyEvent { event ->
                if (event.key == Key.Enter && event.type == KeyEventType.KeyUp && barcodeInput.isNotBlank()) {
                    onEvent(PosEvent.ScanBarcode(barcodeInput.trim()))
                    barcodeInput = ""
                    true
                } else false
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(PosEvent.SearchByName(it)) },
            label = { Text("Buscar por nombre", color = TaupeGray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TaupeGray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = posTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PeachOrange, strokeWidth = 3.dp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.searchResults, key = { it.id ?: it.name }) { product ->
                    ProductResultCard(product = product, onAdd = { onEvent(PosEvent.AddItem(product, 1)) })
                }
            }
        }
    }
}

@Composable
private fun ProductResultCard(product: Product, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        backgroundColor = BoneWhite,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Medium, color = CharcoalBrown, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text("$${"%.2f".format(product.price)}  ·  Stock: ${product.stock}", fontSize = 12.sp, color = TaupeGray)
            }
            Button(
                onClick = onAdd,
                enabled = product.stock > 0,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PeachOrange,
                    contentColor = BoneWhite,
                    disabledBackgroundColor = DarkSand,
                    disabledContentColor = TaupeGray
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Agregar", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun posTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    backgroundColor = SandBeige,
    focusedBorderColor = PeachOrange,
    unfocusedBorderColor = DarkSand,
    cursorColor = PeachOrange,
    textColor = CharcoalBrown
)