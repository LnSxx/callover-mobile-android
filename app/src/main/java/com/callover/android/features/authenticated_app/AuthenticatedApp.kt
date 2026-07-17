package com.callover.android.features.authenticated_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.callover.android.features.call_overlay_host.CallOverlayHost
import com.callover.android.features.main.MainScreen

@Composable
fun AuthenticatedApp() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        MainScreen()

        CallOverlayHost()
    }
}