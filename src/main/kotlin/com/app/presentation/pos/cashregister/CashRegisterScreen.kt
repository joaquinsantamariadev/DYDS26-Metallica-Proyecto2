package com.app.presentation.pos.cashregister

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.SessionStatus
import com.app.presentation.utils.*
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

@Composable
fun CashRegisterScreen(viewModel: CashRegisterViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(SandBeige).padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ArrowBack, null, tint = CharcoalBrown)
            }
            Spacer(Modifier.width(8.dp))
            Text("Sesión de caja", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = CharcoalBrown)
        }

        Spacer(Modifier.height(24.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PeachOrange, strokeWidth = 3.dp)
            }
        } else {
            state.error?.let { error ->
                Text(error, color = PeachOrange, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
            }

            if (state.activeSession == null) {
                OpenSessionForm(onOpen = { viewModel.onEvent(CashRegisterEvent.OpenSession(it)) })
            } else {
                ActiveSessionPanel(
                    session = state.activeSession!!,
                    onClose = { viewModel.onEvent(CashRegisterEvent.CloseSession(it)) }
                )
            }

            Spacer(Modifier.height(24.dp))

            if (state.sessionHistory.isNotEmpty()) {
                Text("Historial", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBrown)
                Spacer(Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = BoneWhite,
                    elevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn {
                        items(state.sessionHistory) { session ->
                            SessionHistoryRow(session)
                            Divider(color = DarkSand.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenSessionForm(onOpen: (Double) -> Unit) {
    var input by remember { mutableStateOf("") }
    val amount = input.toDoubleOrNull()

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = BoneWhite,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Abrir caja", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBrown)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Monto inicial", color = TaupeGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = cashTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { amount?.let { onOpen(it) } },
                enabled = amount != null && amount >= 0.0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PeachOrange,
                    contentColor = BoneWhite,
                    disabledBackgroundColor = DarkSand,
                    disabledContentColor = TaupeGray
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.LockOpen, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Abrir caja")
            }
        }
    }
}

@Composable
private fun ActiveSessionPanel(session: CashRegisterSession, onClose: (Double) -> Unit) {
    var input by remember { mutableStateOf("") }
    val amount = input.toDoubleOrNull()

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = BoneWhite,
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Turno activo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CharcoalBrown)
            Spacer(Modifier.height(12.dp))
            SessionDetailRow("Apertura", session.openedAt.format(timeFormatter))
            SessionDetailRow("Monto inicial", "$${"%.2f".format(session.openingAmount)}")
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Monto de cierre", color = TaupeGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = cashTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { amount?.let { onClose(it) } },
                enabled = amount != null && amount >= 0.0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = PeachOrange,
                    contentColor = BoneWhite,
                    disabledBackgroundColor = DarkSand,
                    disabledContentColor = TaupeGray
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cerrar caja")
            }
        }
    }
}

@Composable
private fun SessionDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TaupeGray, fontSize = 13.sp)
        Text(value, color = CharcoalBrown, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SessionHistoryRow(session: CashRegisterSession) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Icon(
            if (session.status == SessionStatus.OPEN) Icons.Default.LockOpen else Icons.Default.Lock,
            null,
            tint = if (session.status == SessionStatus.OPEN) PeachOrange else TaupeGray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(session.openedAt.format(timeFormatter), color = CharcoalBrown, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            session.closedAt?.let {
                Text("Cerrada: ${it.format(timeFormatter)}", color = TaupeGray, fontSize = 12.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Apertura: $${"%.2f".format(session.openingAmount)}", color = TaupeGray, fontSize = 12.sp)
            session.closingAmount?.let {
                Text("Cierre: $${"%.2f".format(it)}", color = CharcoalBrown, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun cashTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    backgroundColor = SandBeige,
    focusedBorderColor = PeachOrange,
    unfocusedBorderColor = DarkSand,
    cursorColor = PeachOrange,
    textColor = CharcoalBrown
)