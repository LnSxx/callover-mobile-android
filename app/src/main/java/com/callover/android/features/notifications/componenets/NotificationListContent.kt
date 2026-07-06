package com.callover.android.features.notifications.componenets

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.callover.android.R
import com.callover.android.core.domain.models.Notification
import com.callover.android.ui.components.LabeledDivider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun LazyListScope.NotificationsListContent(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
) {
    val grouped = notifications.toNotificationListItems()

    grouped.forEach { item ->
        when (item) {
            is NotificationListUiItem.DateDivider -> {
                item(
                    key = "date-${item.date}",
                ) {
                    NotificationDateDivider(
                        date = item.date,
                    )
                }
            }

            is NotificationListUiItem.NotificationItem -> {
                item(
                    key = "notification-${item.notification.id}",
                ) {
                    NotificationListItem(
                        notification = item.notification,
                        onClick = {
                            onNotificationClick(item.notification)
                        },
                    )
                }
            }
        }
    }
}

private sealed interface NotificationListUiItem {
    data class DateDivider(
        val date: LocalDate,
    ) : NotificationListUiItem

    data class NotificationItem(
        val notification: Notification,
    ) : NotificationListUiItem
}

private fun List<Notification>.toNotificationListItems(): List<NotificationListUiItem> {
    return sortedByDescending { notification ->
        notification.createdAt.toInstantOrNull()
    }
        .groupBy { notification ->
            notification.createdAt
                .toInstantOrNull()
                ?.atZone(ZoneId.systemDefault())
                ?.toLocalDate()
                ?: LocalDate.MIN
        }
        .flatMap { (date, notifications) ->
            listOf(NotificationListUiItem.DateDivider(date)) +
                    notifications.map { notification ->
                        NotificationListUiItem.NotificationItem(notification)
                    }
        }
}

@Composable
private fun NotificationDateDivider(
    date: LocalDate,
) {
    val configuration = LocalConfiguration.current
    val locale = configuration.locales[0]
    val zoneId = ZoneId.systemDefault()

    val todayLabel = stringResource(R.string.notification_group_today)
    val yesterdayLabel = stringResource(R.string.notification_group_yesterday)
    val unknownDateLabel = stringResource(R.string.notification_group_unknown_date)

    LabeledDivider(
        text = date.toNotificationDateLabel(
            locale = locale,
            zoneId = zoneId,
            todayLabel = todayLabel,
            yesterdayLabel = yesterdayLabel,
            unknownDateLabel = unknownDateLabel,
        ),
    )
}

private fun String.toInstantOrNull(): Instant? {
    return runCatching {
        Instant.parse(this)
    }.getOrNull()
}

private fun LocalDate.toNotificationDateLabel(
    locale: Locale,
    zoneId: ZoneId,
    todayLabel: String,
    yesterdayLabel: String,
    unknownDateLabel: String,
): String {
    val today = LocalDate.now(zoneId)
    val yesterday = today.minusDays(1)

    return when (this) {
        today -> todayLabel
        yesterday -> yesterdayLabel
        LocalDate.MIN -> unknownDateLabel
        else -> format(
            DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(locale)
        )
    }
}