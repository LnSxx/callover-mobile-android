package com.callover.android.features.call_overlay_host

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.core.calls.CallState
import com.callover.android.core.domain.models.CallType
import com.callover.android.features.call_overlay_host.screens.AudioCallScreen
import com.callover.android.features.call_overlay_host.screens.IncomingCallScreen
import com.callover.android.features.call_overlay_host.screens.OutgoingCallScreen
import com.callover.android.features.call_overlay_host.screens.VideoCallScreen

@Composable
fun CallOverlayHost(
    modifier: Modifier = Modifier,
    viewModel: CallViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val callState = uiState.callState) {
        CallState.Idle -> Unit

        is CallState.Incoming -> {
            IncomingCallScreen(
                modifier = modifier.fillMaxSize(),
                callState = callState,
                contactName = uiState.peerDisplayName,
                onAcceptClick = viewModel::acceptCall,
                onDeclineClick = viewModel::declineCall,
            )
        }

        is CallState.Outgoing -> {
            OutgoingCallScreen(
                modifier = modifier.fillMaxSize(),
                callState = callState,
                contactName = uiState.peerDisplayName,
                onCancelClick = viewModel::cancelOutgoingCall,
            )
        }

        is CallState.Connecting -> {
            if (callState.type == CallType.Video) {
                VideoCallScreen(
                    modifier = modifier.fillMaxSize(),
                    callState = callState,
                    contactName = uiState.peerDisplayName,
                    onEndClick = viewModel::endCall,
                    onToggleMicClick = viewModel::toggleMic,
                    onToggleCameraClick = viewModel::toggleCamera,
                )
            } else {
                AudioCallScreen(
                    modifier = modifier.fillMaxSize(),
                    callState = callState,
                    contactName = uiState.peerDisplayName,
                    onEndClick = viewModel::endCall,
                    onToggleMicClick = viewModel::toggleMic,
                )
            }
        }

        is CallState.Active -> {
            if (callState.type == CallType.Video) {
                VideoCallScreen(
                    modifier = modifier.fillMaxSize(),
                    callState = callState,
                    contactName = uiState.peerDisplayName,
                    onEndClick = viewModel::endCall,
                    onToggleMicClick = viewModel::toggleMic,
                    onToggleCameraClick = viewModel::toggleCamera,
                )
            } else {
                AudioCallScreen(
                    modifier = modifier.fillMaxSize(),
                    callState = callState,
                    contactName = uiState.peerDisplayName,
                    onEndClick = viewModel::endCall,
                    onToggleMicClick = viewModel::toggleMic,
                )
            }
        }

        is CallState.Ended -> Unit
    }
}