package com.callover.android.features.settings.components

import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.callover.android.R
import com.callover.android.ui.components.LabeledDivider
import kotlinx.coroutines.launch

@Composable
fun ProfileInfo(
    username: String,
    userId: String,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            username,
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        LabeledDivider(
            text = stringResource(R.string.your_profile_id)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = userId,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, userId)
                    }

                    context.startActivity(
                        Intent.createChooser(sendIntent, "Share Profile ID")
                    )
                }
            ) {
                Text(stringResource(R.string.share))
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        clipboardManager.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText("Profile ID", userId)
                            )
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.copy))
            }
        }
    }
}


@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=691dp",
)
@Composable
fun ProfileInfoPreview() {
    ProfileInfo(
        username = "Catherine II the Great",
        userId = "dpad2-f23-ff2f2f-24ffi-fbdfb",
    )
}