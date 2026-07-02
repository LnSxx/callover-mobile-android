package com.callover.android.core.network

data class ApiFieldError(
    val field: String,
    val code: String,
    val message: String,
)