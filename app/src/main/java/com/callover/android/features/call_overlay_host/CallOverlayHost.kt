package com.callover.android.features.call_overlay_host

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.callover.android.core.calls.CallState
import com.callover.android.core.domain.models.CallType
import com.callover.android.core.permissions.CallMediaPermissions
import com.callover.android.core.permissions.requiredPermissions
import com.callover.android.features.call_overlay_host.screens.AudioCallScreen
import com.callover.android.features.call_overlay_host.screens.IncomingCallScreen
import com.callover.android.features.call_overlay_host.screens.OutgoingCallScreen
import com.callover.android.features.call_overlay_host.screens.VideoCallScreen

@Composable
fun CallOverlayHost(
    modifier: Modifier = Modifier,
    viewModel: CallViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var pendingAcceptCallType by rememberSaveable {
        mutableStateOf<CallType?>(null)
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val callType = pendingAcceptCallType
        pendingAcceptCallType = null

        if (callType == null) {
            return@rememberLauncherForActivityResult
        }

        val hasMicrophone =
            permissions[Manifest.permission.RECORD_AUDIO] == true ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO,
                    ) == PackageManager.PERMISSION_GRANTED

        val hasCamera =
            permissions[Manifest.permission.CAMERA] == true ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA,
                    ) == PackageManager.PERMISSION_GRANTED

        val granted = CallMediaPermissions(
            hasMicrophone = hasMicrophone,
            hasCamera = hasCamera,
        ).isGrantedFor(callType)

        if (granted) {
            viewModel.acceptCall()
        } else {
            viewModel.onMediaPermissionDenied()
        }
    }

    when (val callState = uiState.callState) {
        CallState.Idle -> Unit

        is CallState.Incoming -> {
            IncomingCallScreen(
                modifier = modifier.fillMaxSize(),
                callState = callState,
                contactName = uiState.peerDisplayName,
                onAcceptClick = {
                    pendingAcceptCallType = callState.type
                    permissionsLauncher.launch(
                        callState.type.requiredPermissions(),
                    )
                },
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
                    remoteVideoTrack = uiState.mediaState.remoteVideoTrack,
                    localVideoTrack = uiState.mediaState.localVideoTrack,
                    eglBaseContext = viewModel.eglBaseContext,
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
                    remoteVideoTrack = uiState.mediaState.remoteVideoTrack,
                    localVideoTrack = uiState.mediaState.localVideoTrack,
                    eglBaseContext = viewModel.eglBaseContext,
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