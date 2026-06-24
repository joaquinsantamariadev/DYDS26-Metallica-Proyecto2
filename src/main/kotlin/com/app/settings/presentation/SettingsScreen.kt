package com.app.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.common.presentation.utils.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Local") }

    Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        
        Surface(
            modifier = Modifier.width(250.dp).fillMaxHeight(),
            elevation = 0.dp,
            color = MaterialTheme.colors.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configuración", 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                MenuButton(
                    text = "Local", 
                    icon = Icons.Default.Store, 
                    isSelected = selectedTab == "Local",
                    onClick = { selectedTab = "Local" }
                )
                MenuButton(
                    text = "Sistema", 
                    icon = Icons.Default.Settings, 
                    isSelected = selectedTab == "Sistema",
                    onClick = { selectedTab = "Sistema" }
                )
                MenuButton(
                    text = "Exportar Datos", 
                    icon = Icons.Default.Download, 
                    isSelected = selectedTab == "Datos",
                    onClick = { selectedTab = "Datos" }
                )
            }
        }


        Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(32.dp)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = PeachOrange, 
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxSize(), 
                    elevation = 0.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        uiState.errorMessage?.let { error ->
                            Text(text = error, color = Color(0xFFD32F2F), modifier = Modifier.padding(bottom = 16.dp))
                        }

                        when (selectedTab) {
                            "Local" -> StoreSettingsForm(
                                settings = uiState.storeSettings,
                                onSave = { viewModel.saveStoreSettings(it) }
                            )
                            "Sistema" -> SystemSettingsForm(
                                settings = uiState.systemSettings,
                                onSave = { viewModel.saveSystemSettings(it) }
                            )
                            "Datos" -> ExportPanel(
                                onExport = { path, format -> viewModel.exportData(path, format) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun MenuButton(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = if (isSelected) PeachOrange.copy(alpha = 0.15f) else Color.Transparent,
            contentColor = if (isSelected) PeachOrange else TaupeGray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, modifier = Modifier.padding(start = 8.dp, end = 16.dp).size(20.dp))
            Text(text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
