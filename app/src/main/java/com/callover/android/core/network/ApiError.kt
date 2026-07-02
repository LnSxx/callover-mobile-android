package com.callover.android.core.network

sealed interface ApiError {
    data class Backend(
        val statusCode: Int,
        val code: String,
        val message: String,
        val fieldErrors: List<ApiFieldError> = emptyList(),
    ) : ApiError

    data object Unauthorized : ApiError

    data object Network : ApiError

    data object Unknown : ApiError
}