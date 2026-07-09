package com.callover.android.core.network

import com.callover.android.core.data.account.AccountApi
import com.callover.android.core.data.auth.AuthApi
import com.callover.android.core.data.contacts.ContactsApi
import com.callover.android.core.data.notifications.NotificationsApi
import com.callover.android.core.data.profile.ProfileApi
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cookieJar: PersistentCalloverCookieJar,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit,
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(
        retrofit: Retrofit,
    ): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationsApi(
        retrofit: Retrofit,
    ): NotificationsApi {
        return retrofit.create(NotificationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideContactsApi(
        retrofit: Retrofit,
    ): ContactsApi {
        return retrofit.create(ContactsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountApi(
        retrofit: Retrofit,
    ): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }
}