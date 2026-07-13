package com.callover.android.core.call_coordinator

import com.callover.android.core.realtime.signaling.SignalingEvent
import com.callover.android.core.realtime.signaling.SignalingRealtimeDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallCoordinator @Inject constructor(
    private val signalingRealtimeDataSource: SignalingRealtimeDataSource,
) {
    suspend fun collectSignalingEvents() {
        signalingRealtimeDataSource.events.collect { event ->
            when (event) {
                is SignalingEvent.CallOffer -> {
                    // TODO incoming call
                }

                is SignalingEvent.CallAnswer -> {
                    // TODO set remote answer
                }

                is SignalingEvent.CallDecline -> {
                    // TODO outgoing declined
                }

                is SignalingEvent.CallCancel -> {
                    // TODO incoming cancelled
                }

                is SignalingEvent.CallEnd -> {
                    // TODO peer ended call
                }

                is SignalingEvent.CallTimeout -> {
                    // TODO timeout
                }

                is SignalingEvent.CallIceCandidate -> {
                    // TODO add or buffer ICE
                }
            }
        }
    }
}