package com.callover.android.features.call_overlay_host.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.callover.android.R
import com.callover.android.core.calls.CallState
import com.callover.android.core.formatters.CallDurationFormatter
import kotlinx.coroutines.delay
import java.time.Instant

@Composable
fun CallStatusText(
    callState: CallState,
) {
    var nowEpochSecond by remember {
        mutableLongStateOf(Instant.now().epochSecond)
    }

    LaunchedEffect(callState) {
        if (callState !is CallState.Active) {
            return@LaunchedEffect
        }

        while (true) {
            nowEpochSecond = Instant.now().epochSecond
            delay(1_000)
        }
    }

    val text = when (callState) {
        is CallState.Connecting -> {
            stringResource(R.string.connecting_three_dots)
        }

        is CallState.Active -> {
            CallDurationFormatter.format(
                startedAt = callState.startedAt,
                now = Instant.ofEpochSecond(nowEpochSecond),
            )
        }

        else -> ""
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}