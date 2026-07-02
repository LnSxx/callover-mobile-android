package com.callover.android.core.di

import com.callover.android.core.data.auth.AuthRepository
import com.callover.android.core.data.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository
}