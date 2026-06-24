package com.app.inventory.presentation

import androidx.compose.material.MaterialTheme

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.inventory.domain.entity.Category
import com.app.common.presentation.utils.BoneWhite
import com.app.common.presentation.utils.CharcoalBrown
import com.app.common.presentation.utils.DarkSand
import com.app.common.presentation.utils.PeachOrange
import com.app.common.presentation.utils.SandBeige
import com.app.common.presentation.utils.TaupeGray

@Composable
fun CategoryScreen(viewModel: CategoryViewModel) {
    val state by viewModel.state.collectAsState()
    var newCategoryName by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(24.dp)) {
        Text("Categorías", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onSurface)
        Spacer(Modifier.height(4.dp))
        Text("Administración de rubros", fontSize = 14.sp, color = TaupeGray)
        Spacer(Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(16.dp), backgroundColor = MaterialTheme.colors.surface, elevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Nueva categoría...", color = TaupeGray) },
                    leadingIcon = { Icon(Icons.Default.Category, null, tint = TaupeGray) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        focusedBorderColor = PeachOrange,
                        unfocusedBorderColor = DarkSand,
                        cursorColor = PeachOrange,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = { viewModel.addCategory(newCategoryName.trim()); newCategoryName = "" },
                    enabled = newCategoryName.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = PeachOrange,
                        contentColor = MaterialTheme.colors.surface,
                        disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        disabledContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PeachOrange, strokeWidth = 3.dp)
            }
            state.categories.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Category, null, modifier = Modifier.size(72.dp), tint = DarkSand)
                    Spacer(Modifier.height(12.dp))
                    Text("Sin categorías todavía", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TaupeGray)
                }
            }
            else -> Card(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn {
                    items(state.categories) { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
                        ) {
                            Text(category.name, color = MaterialTheme.colors.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { editingCategory = category }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, null, tint = TaupeGray, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { deletingCategory = category }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Delete, null, tint = PeachOrange, modifier = Modifier.size(16.dp))
                            }
                        }
                        Divider(color = DarkSand.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    editingCategory?.let { category ->
        var editedName by remember { mutableStateOf(category.name) }
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Editar categoría", color = MaterialTheme.colors.onSurface, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre", color = TaupeGray) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colors.background,
                        focusedBorderColor = PeachOrange,
                        unfocusedBorderColor = DarkSand,
                        cursorColor = PeachOrange,
                        textColor = MaterialTheme.colors.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.updateCategory(category.copy(name = editedName)); editingCategory = null },
                    colors = ButtonDefaults.buttonColors(backgroundColor = PeachOrange, contentColor = MaterialTheme.colors.surface)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) { Text("Cancelar", color = TaupeGray) }
            },
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    deletingCategory?.let { category ->
        AlertDialog(
            onDismissRequest = { deletingCategory = null },
            title = { Text("Eliminar categoría", color = MaterialTheme.colors.onSurface) },
            text = { Text("¿Confirmás que querés eliminar \"${category.name}\"?", color = TaupeGray) },
            confirmButton = {
                Button(
                    onClick = { category.id?.let { viewModel.deleteCategory(it) }; deletingCategory = null },
                    colors = ButtonDefaults.buttonColors(backgroundColor = PeachOrange, contentColor = MaterialTheme.colors.surface)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { deletingCategory = null }) { Text("Cancelar", color = TaupeGray) }
            },
            backgroundColor = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}