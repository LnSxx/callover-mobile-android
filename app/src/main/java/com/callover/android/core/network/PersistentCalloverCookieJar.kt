package com.callover.android.core.network

import com.callover.android.core.storage.cookie.CookieStorage
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersistentCalloverCookieJar @Inject constructor(
    private val cookieStorage: CookieStorage,
) : CookieJar {
    private val cookies = mutableListOf<Cookie>()

    init {
        cookies.addAll(
            cookieStorage
                .loadCookies()
                .map { it.toOkHttpCookie() }
                .filter { !cookieHasExpired(it) },
        )

        persistCookies()
    }

    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>,
    ) {
        this.cookies.removeAll { savedCookie ->
            cookies.any { newCookie ->
                savedCookie.name == newCookie.name &&
                        savedCookie.domain == newCookie.domain &&
                        savedCookie.path == newCookie.path
            }
        }

        this.cookies.addAll(
            cookies.filter { !cookieHasExpired(it) },
        )

        persistCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val removedExpired = cookies.removeAll { cookie ->
            cookieHasExpired(cookie)
        }

        if (removedExpired) {
            persistCookies()
        }

        return cookies.filter { cookie ->
            cookie.matches(url) && !cookieHasExpired(cookie)
        }
    }

    fun clear() {
        cookies.clear()
        cookieStorage.clearCookies()
    }

    private fun persistCookies() {
        cookieStorage.saveCookies(
            cookies
                .filter { !cookieHasExpired(it) }
                .map { it.toStoredCookie() },
        )
    }

    private fun cookieHasExpired(cookie: Cookie): Boolean {
        return cookie.expiresAt < System.currentTimeMillis()
    }
}