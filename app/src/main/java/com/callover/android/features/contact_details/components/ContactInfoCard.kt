package com.callover.android.features.contact_details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ContactInfoCard(
    name: String,
    userId: String,
    isFavourite: Boolean,
    isBlocked: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
    isOnline: Boolean = false,
) {
    ContactFavouriteWrapper(
        isFavourite = isFavourite,
        modifier = modifier.fillMaxWidth()
    ) {
        ContactInfoCardContent(
            name = name,
            userId = userId,
            isBlocked = isBlocked,
            isMuted = isMuted,
            isOnline = isOnline,
        )
    }
}