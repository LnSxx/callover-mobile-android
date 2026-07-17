package com.callover.android.core.di

import com.callover.android.core.data.account.AccountRepository
import com.callover.android.core.data.account.AccountRepositoryImpl
import com.callover.android.core.data.auth.AuthRepository
import com.callover.android.core.data.auth.AuthRepositoryImpl
import com.callover.android.core.data.calls.CallsRepository
import com.callover.android.core.data.calls.CallsRepositoryImpl
import com.callover.android.core.data.contacts.ContactsRepository
import com.callover.android.core.data.contacts.ContactsRepositoryImpl
import com.callover.android.core.data.notifications.NotificationsRepository
import com.callover.android.core.data.notifications.NotificationsRepositoryImpl
import com.callover.android.core.data.profile.ProfileRepository
import com.callover.android.core.data.profile.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl,
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(
        implementation: NotificationsRepositoryImpl,
    ): NotificationsRepository

    @Binds
    @Singleton
    abstract fun bindContactsRepository(
        implementation: ContactsRepositoryImpl,
    ): ContactsRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        implementation: AccountRepositoryImpl,
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCallsRepository(
        implementation: CallsRepositoryImpl,
    ): CallsRepository
}