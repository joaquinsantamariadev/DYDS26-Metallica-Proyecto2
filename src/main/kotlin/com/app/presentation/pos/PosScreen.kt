package com.app.presentation.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.presentation.pos.components.CartPanel
import com.app.presentation.pos.components.PaymentDialog
import com.app.presentation.pos.components.ProductSearchPanel
import com.app.presentation.utils.BoneWhite
import com.app.presentation.utils.DarkSand
import com.app.presentation.utils.SandBeige

@Composable
fun PosScreen(viewModel: PosViewModel) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    var showPaymentDialog by remember { mutableStateOf(false) }

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
        Row(modifier = Modifier.fillMaxSize().background(SandBeige)) {
            ProductSearchPanel(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = Modifier.weight(0.55f).background(BoneWhite)
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

    if (showPaymentDialog) {
        PaymentDialog(
            onConfirm = { viewModel.onEvent(PosEvent.CompleteSale(it)) },
            onDismiss = { showPaymentDialog = false }
        )
    }
}