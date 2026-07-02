package com.callover.android.core.network

import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    json: Json,
    call: suspend () -> T,
): ApiResult<T> {
    return try {
        ApiResult.Success(call())
    } catch (error: HttpException) {
        ApiResult.Error(parseHttpError(json, error))
    } catch (_: IOException) {
        ApiResult.Error(ApiError.Network)
    } catch (_: Throwable) {
        ApiResult.Error(ApiError.Unknown)
    }
}