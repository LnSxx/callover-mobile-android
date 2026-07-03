package com.callover.android.features.settings

data class SettingsUiState(
    val username: String = "",
    val userId: String = "",
    val isLoading: Boolean = true,
)