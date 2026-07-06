package com.callover.android.features.contacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callover.android.core.domain.models.Contact
import com.callover.android.ui.components.LabeledDivider
import java.text.Collator
import java.util.Locale

@Composable
fun ContactsList(
    contacts: List<Contact>,
) {
    val groupedContacts = remember(contacts) {
        val collator = Collator.getInstance(Locale.getDefault())

        contacts
            .sortedWith { first, second ->
                collator.compare(first.sortName(), second.sortName())
            }
            .groupBy { contact ->
                contact.groupTitle()
            }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        groupedContacts.forEach { (title, contacts) ->
            LabeledDivider(
                text = title
            )

            contacts.forEach { contact ->
                ContactListItem(
                    contact = contact,
                    // TODO: Implement Presence State to see who is online and pass here
                    isOnline = false,
                    // TODO: Implement Contact Screen for detailed view and edit/delete features
                    onTap = {}
                )
            }
        }
    }
}

private fun Contact.sortName(): String {
    return alias.trim().ifEmpty { "#" }
}

private fun Contact.groupTitle(): String {
    val firstChar = alias.trim().firstOrNull() ?: return "#"

    return when {
        firstChar.isLetterOrDigit() -> firstChar.uppercaseChar().toString()
        else -> "#"
    }
}