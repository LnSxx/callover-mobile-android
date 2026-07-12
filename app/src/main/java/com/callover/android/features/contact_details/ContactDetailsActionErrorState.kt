package com.callover.android.features.contact_details

import com.callover.android.core.network.ApiError

fun ApiError.toContactDetailsActionError(): String {
    return when (this) {
        is ApiError.Backend -> message
        ApiError.Network -> "Network error. Check your connection."
        ApiError.Unknown -> "Something went wrong."
        ApiError.Unauthorized -> "Session expired. Please log in again."
    }
}