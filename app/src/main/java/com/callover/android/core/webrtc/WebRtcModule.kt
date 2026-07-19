package com.callover.android.core.webrtc

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WebRtcModule {

    @Binds
    @Singleton
    abstract fun bindWebRtcEngine(
        implementation: WebRtcEngineImpl,
    ): WebRtcEngine
}