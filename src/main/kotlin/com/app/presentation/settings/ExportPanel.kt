package com.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.settings.ExportFormat
import javax.swing.JFileChooser

@Composable
fun ExportPanel(
    onExport: (String, ExportFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Exportar Datos", style = MaterialTheme.typography.h6)
        Button(onClick = {
            val fileChooser = JFileChooser()
            fileChooser.dialogTitle = "Seleccionar ubicación de exportación"
            val result = fileChooser.showSaveDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                onExport(fileChooser.selectedFile.absolutePath, ExportFormat.CSV)
            }
        }) {
            Text("Exportar a CSV")
        }
    }
}
