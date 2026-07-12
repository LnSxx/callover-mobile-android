package com.callover.android.features.contact_details.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.callover.android.R
import com.callover.android.ui.theme.CalloverMobileTheme
import com.callover.android.ui.theme.OnTertiaryContainerDark
import com.callover.android.ui.theme.OnTertiaryDark
import com.callover.android.ui.theme.TertiaryDark

@Composable
fun ContactFavouriteWrapper(
    isFavourite: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    val isDark = isSystemInDarkTheme()

    val favouriteContainerColor = if (isDark) {
        TertiaryDark.copy(
            alpha = 0.6F
        )
    } else {
        TertiaryDark
    }

    val favouriteContentColor = if (isDark) {
        OnTertiaryContainerDark
    } else {
        OnTertiaryDark
    }

    if (isFavourite) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = favouriteContainerColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                content()

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = favouriteContentColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        stringResource(R.string.contact_modifier_favourite),
                        style = MaterialTheme.typography.labelLarge,
                        color = favouriteContentColor,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    } else {
        content()
    }
}

@Preview
@Composable
fun ContactFavouriteWrapperPreview() {
    CalloverMobileTheme() {
        ContactFavouriteWrapper(
            isFavourite = true,
        ) {
            ContactInfoCardContent(
                name = "Catherine II the Great Empress or Russia",
                userId = "qwer-tyui-opas-dfgh",
                isBlocked = false,
                isMuted = true,
                isOnline = true,
            )
        }
    }
}

@Preview
@Composable
fun ContactFavouriteWrapper_Dark_Preview() {
    CalloverMobileTheme(
        darkTheme = true
    ) {
        ContactFavouriteWrapper(
            isFavourite = true,
        ) {
            ContactInfoCardContent(
                name = "Catherine II the Great Empress or Russia",
                userId = "qwer-tyui-opas-dfgh",
                isBlocked = false,
                isMuted = true,
                isOnline = true,
            )
        }
    }
}