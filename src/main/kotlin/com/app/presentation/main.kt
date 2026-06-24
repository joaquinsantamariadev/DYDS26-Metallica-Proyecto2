package com.app.presentation

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.app.common.data.local.DatabaseFactory
import com.app.di.appModule
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    
    DatabaseFactory.init()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DYDS - Proyecto 2"
        ) {
            App()
        }
    }
}
