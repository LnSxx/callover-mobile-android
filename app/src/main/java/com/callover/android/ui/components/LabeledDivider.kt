package com.callover.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabeledDivider(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}