package com.app.pos.presentation

import androidx.compose.material.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.pos.presentation.cashregister.CashRegisterScreen
import com.app.pos.presentation.cashregister.CashRegisterViewModel
import com.app.pos.presentation.components.CartPanel
import com.app.pos.presentation.components.PaymentDialog
import com.app.pos.presentation.components.ProductSearchPanel
import com.app.pos.presentation.components.SessionStatusBar
import com.app.common.presentation.utils.BoneWhite
import com.app.common.presentation.utils.DarkSand
import com.app.common.presentation.utils.SandBeige

@Composable
fun PosScreen(viewModel: PosViewModel, cashRegisterViewModel: CashRegisterViewModel) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showCashRegister by remember { mutableStateOf(false) }

    if (showCashRegister) {
        CashRegisterScreen(
            viewModel = cashRegisterViewModel,
            onBack = {
                showCashRegister = false
                viewModel.onEvent(PosEvent.RefreshSession)
            }
        )
        return
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            scaffoldState.snackbarHostState.showSnackbar(it)
            viewModel.onEvent(PosEvent.DismissError)
        }
    }

    LaunchedEffect(state.saleCompleted) {
        if (state.saleCompleted) {
            showPaymentDialog = false
            viewModel.onEvent(PosEvent.AcknowledgeSale)
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            SessionStatusBar(
                activeSession = state.activeSession,
                onManageSession = { showCashRegister = true }
            )
            Row(modifier = Modifier.weight(1f)) {
                ProductSearchPanel(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.weight(0.55f).background(MaterialTheme.colors.surface)
                )
                Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(DarkSand))
                CartPanel(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onCheckout = { showPaymentDialog = true },
                    modifier = Modifier.weight(0.45f)
                )
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            onConfirm = { viewModel.onEvent(PosEvent.CompleteSale(it)) },
            onDismiss = { showPaymentDialog = false }
        )
    }
}