package com.callover.android.core.network

import com.callover.android.core.network.dto.BackendErrorDto
import kotlinx.serialization.json.Json
import retrofit2.HttpException

fun parseHttpError(
    json: Json,
    error: HttpException,
): ApiError {
    if (error.code() == 401) {
        return ApiError.Unauthorized
    }

    val rawBody = error.response()?.errorBody()?.string()

    if (rawBody.isNullOrBlank()) {
        return ApiError.Backend(
            statusCode = error.code(),
            code = "HTTP_ERROR",
            message = "Request failed",
        )
    }

    return try {
        val backendError = json.decodeFromString<BackendErrorDto>(rawBody)

        ApiError.Backend(
            statusCode = backendError.statusCode,
            code = backendError.code,
            message = backendError.message,
            fieldErrors = backendError.errors.map {
                ApiFieldError(
                    field = it.field,
                    code = it.code,
                    message = it.message,
                )
            },
        )
    } catch (_: Throwable) {
        ApiError.Backend(
            statusCode = error.code(),
            code = "HTTP_ERROR",
            message = "Request failed",
        )
    }
}