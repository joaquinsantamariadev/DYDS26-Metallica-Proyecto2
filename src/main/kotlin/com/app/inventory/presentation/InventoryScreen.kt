package com.app.inventory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.inventory.domain.entity.Product
import com.app.common.presentation.utils.BoneWhite
import com.app.common.presentation.utils.CharcoalBrown
import com.app.common.presentation.utils.CoffeeBrown
import com.app.common.presentation.utils.DarkSand
import com.app.common.presentation.utils.LightPeach
import com.app.common.presentation.utils.PeachOrange
import com.app.common.presentation.utils.SandBeige
import com.app.common.presentation.utils.TaupeGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val state by viewModel.state.collectAsState()
    var barcodeInput by remember { mutableStateOf("") }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var deletingProduct by remember { mutableStateOf<Product?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(SandBeige).padding(24.dp)
    ) {
        Text("Inventario", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = CharcoalBrown)
        Spacer(Modifier.height(4.dp))
        Text("Gestión de productos y stock", fontSize = 14.sp, color = TaupeGray)
        Spacer(Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(16.dp), backgroundColor = BoneWhite, elevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.search(it) },
                        placeholder = { Text("Buscar por nombre o código...", color = TaupeGray) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TaupeGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = SandBeige,
                            focusedBorderColor = PeachOrange,
                            unfocusedBorderColor = DarkSand,
                            cursorColor = PeachOrange,
                            textColor = CharcoalBrown
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    OutlinedTextField(
                        value = barcodeInput,
                        onValueChange = { barcodeInput = it },
                        placeholder = { Text("Código de barras...", color = TaupeGray) },
                        leadingIcon = { Icon(Icons.Default.QrCodeScanner, null, tint = TaupeGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = SandBeige,
                            focusedBorderColor = PeachOrange,
                            unfocusedBorderColor = DarkSand,
                            cursorColor = PeachOrange,
                            textColor = CharcoalBrown
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { viewModel.scanBarcode(barcodeInput.trim()); barcodeInput = "" },
                        enabled = barcodeInput.isNotBlank() && !state.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = PeachOrange,
                            contentColor = BoneWhite,
                            disabledBackgroundColor = DarkSand,
                            disabledContentColor = TaupeGray
                        ),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Buscar")
                    }
                }
            }
        }

        AnimatedVisibility(visible = state.error != null, enter = fadeIn(), exit = fadeOut()) {
            state.error?.let { errorMsg ->
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp)).background(PeachOrange.copy(alpha = 0.15f)).padding(12.dp)
                ) {
                    Text(errorMsg, color = CoffeeBrown, fontSize = 13.sp)
                }
            }
        }

        if (state.exchangeRateUnavailable) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp)).background(DarkSand.copy(alpha = 0.4f)).padding(10.dp)
            ) {
                Text(
                    text = "Cotización no disponible — precios en ARS no pueden calcularse",
                    color = TaupeGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PeachOrange, strokeWidth = 3.dp)
            }
            state.products.isEmpty() -> EmptyInventoryPlaceholder()
            else -> ProductTable(
                products = state.products,
                exchangeRate = state.exchangeRate,
                onEdit = { editingProduct = it },
                onDelete = { deletingProduct = it }
            )
        }
    }

    editingProduct?.let { product ->
        EditProductDialog(
            product = product,
            onConfirm = { updated -> viewModel.updateProduct(updated); editingProduct = null },
            onDismiss = { editingProduct = null }
        )
    }

    deletingProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { deletingProduct = null },
            title = { Text("Eliminar producto", color = CharcoalBrown) },
            text = { Text("¿Confirmás que querés eliminar \"${product.name}\"?", color = TaupeGray) },
            confirmButton = {
                Button(
                    onClick = { product.id?.let { viewModel.deleteProduct(it) }; deletingProduct = null },
                    colors = ButtonDefaults.buttonColors(backgroundColor = PeachOrange, contentColor = BoneWhite)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { deletingProduct = null }) { Text("Cancelar", color = TaupeGray) }
            },
            backgroundColor = BoneWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun EditProductDialog(product: Product, onConfirm: (Product) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var cost by remember { mutableStateOf(product.cost.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var expiryDate by remember { mutableStateOf(product.expiryDate?.format(dateFormatter) ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar producto", color = CharcoalBrown, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DialogField("Nombre", name) { name = it }
                DialogField("Precio (USD)", price) { price = it }
                DialogField("Costo (USD)", cost) { cost = it }
                DialogField("Stock", stock) { stock = it }
                DialogField("Vencimiento (dd/MM/yyyy)", expiryDate) { expiryDate = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = runCatching { LocalDate.parse(expiryDate, dateFormatter) }.getOrNull()
                    onConfirm(product.copy(
                        name = name,
                        price = price.toDoubleOrNull() ?: product.price,
                        cost = cost.toDoubleOrNull() ?: product.cost,
                        stock = stock.toIntOrNull() ?: product.stock,
                        expiryDate = parsed
                    ))
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = PeachOrange, contentColor = BoneWhite)
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TaupeGray) }
        },
        backgroundColor = BoneWhite,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun DialogField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TaupeGray) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = SandBeige,
            focusedBorderColor = PeachOrange,
            unfocusedBorderColor = DarkSand,
            cursorColor = PeachOrange,
            textColor = CharcoalBrown
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EmptyInventoryPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(72.dp), tint = DarkSand)
            Spacer(Modifier.height(12.dp))
            Text("Sin productos todavía", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TaupeGray)
            Spacer(Modifier.height(4.dp))
            Text("Escaneá un código de barras para comenzar", fontSize = 13.sp, color = TaupeGray)
        }
    }
}

@Composable
private fun ProductTable(
    products: List<Product>,
    exchangeRate: ExchangeRate?,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp), backgroundColor = BoneWhite, elevation = 0.dp, modifier = Modifier.fillMaxSize()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().background(LightPeach).padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text("Producto", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(2f))
                Text("Código", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                Text("Precio (USD)", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("Precio (ARS)", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("Costo (USD)", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("Stock", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(0.7f))
                Text("Vence", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1.2f))
                Spacer(Modifier.weight(0.6f))
            }
            Divider(color = DarkSand, thickness = 1.dp)
            LazyColumn {
                items(products) { product ->
                    ProductRow(product, exchangeRate, onEdit, onDelete)
                    Divider(color = DarkSand.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    product: Product,
    exchangeRate: ExchangeRate?,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    val today = LocalDate.now()
    val isExpired = product.expiryDate?.isBefore(today) == true
    val isExpiringSoon = product.expiryDate?.isBefore(today.plusDays(30)) == true && !isExpired
    val rowBackground = when {
        isExpired -> Color(0xFFFFEBEB)
        isExpiringSoon -> Color(0xFFFFF8E1)
        else -> Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().background(rowBackground).padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(product.name, color = CharcoalBrown, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
        Text(product.barcode ?: "—", color = TaupeGray, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
        Text("$${String.format("%.2f", product.price)}", color = CharcoalBrown, fontSize = 14.sp, modifier = Modifier.weight(1f))
        val arsPrice = exchangeRate?.let { product.price * it.rate }
        Text(
            text = arsPrice?.let { "$${"%.2f".format(it)}" } ?: "—",
            color = TaupeGray,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text("$${String.format("%.2f", product.cost)}", color = TaupeGray, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("${product.stock}", color = CharcoalBrown, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.7f))
        Text(
            text = product.expiryDate?.format(dateFormatter) ?: "—",
            color = when {
                isExpired -> Color(0xFFD32F2F)
                isExpiringSoon -> Color(0xFFF57F17)
                else -> TaupeGray
            },
            fontSize = 13.sp,
            modifier = Modifier.weight(1.2f)
        )
        Row(modifier = Modifier.weight(0.6f), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { onEdit(product) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, null, tint = TaupeGray, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { onDelete(product) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, null, tint = PeachOrange, modifier = Modifier.size(16.dp))
            }
        }
    }
}