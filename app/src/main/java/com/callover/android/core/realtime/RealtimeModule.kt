package com.callover.android.core.realtime

import com.callover.android.core.signaling.SignalingService
import com.callover.android.core.signaling.SignalingServiceImpl
import dagger.hilt.components.SingletonComponent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RealtimeModule {

    @Binds
    @Singleton
    abstract fun bindSignalingService(
        implementation: SignalingServiceImpl,
    ): SignalingService
}