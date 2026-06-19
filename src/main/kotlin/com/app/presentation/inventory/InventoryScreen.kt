package com.app.presentation.inventory

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.domain.entity.Product
import com.app.presentation.utils.BoneWhite
import com.app.presentation.utils.CoffeeBrown
import com.app.presentation.utils.CharcoalBrown
import com.app.presentation.utils.DarkSand
import com.app.presentation.utils.LightPeach
import com.app.presentation.utils.PeachOrange
import com.app.presentation.utils.SandBeige
import com.app.presentation.utils.TaupeGray

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val state by viewModel.state.collectAsState()
    var barcodeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SandBeige)
            .padding(24.dp)
    ) {
        Text(
            text = "Inventario",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalBrown
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Gestión de productos y stock",
            fontSize = 14.sp,
            color = TaupeGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            backgroundColor = BoneWhite,
            elevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = { barcodeInput = it },
                    placeholder = {
                        Text("Ingresá un código de barras...", color = TaupeGray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TaupeGray
                        )
                    },
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

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        viewModel.scanBarcode(barcodeInput.trim())
                        barcodeInput = ""
                    },
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
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar")
                }
            }
        }

        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            state.error?.let { errorMsg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PeachOrange.copy(alpha = 0.15f))
                        .padding(12.dp)
                ) {
                    Text(text = errorMsg, color = CoffeeBrown, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PeachOrange, strokeWidth = 3.dp)
            }
        } else if (state.products.isEmpty()) {
            EmptyInventoryPlaceholder()
        } else {
            ProductTable(products = state.products)
        }
    }
}

@Composable
private fun EmptyInventoryPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = DarkSand
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sin productos todavía",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TaupeGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Escaneá un código de barras para comenzar",
                fontSize = 13.sp,
                color = TaupeGray
            )
        }
    }
}

@Composable
private fun ProductTable(products: List<Product>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = BoneWhite,
        elevation = 0.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightPeach)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text("Producto", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(2f))
                Text("Código", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                Text("Precio (USD)", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("Costo (USD)", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("Stock", fontWeight = FontWeight.SemiBold, color = CharcoalBrown, fontSize = 13.sp, modifier = Modifier.weight(0.7f))
            }

            Divider(color = DarkSand, thickness = 1.dp)

            LazyColumn {
                items(products) { product ->
                    ProductRow(product)
                    Divider(color = DarkSand.copy(alpha = 0.5f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun ProductRow(product: Product) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(product.name, color = CharcoalBrown, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
        Text(product.barcode ?: "—", color = TaupeGray, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
        Text("$${String.format("%.2f", product.price)}", color = CharcoalBrown, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("$${String.format("%.2f", product.cost)}", color = TaupeGray, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("${product.stock}", color = CharcoalBrown, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.7f))
    }
}
