package com.callover.android.features.main

object MainRoutes {
    const val HOME = "home"
    const val HOME_CALL_LOGS = "home_call_logs"

    const val CONTACTS = "contacts"
    const val CONTACTS_CREATE_CONTACT = "contacts_create_contact"
    const val CONTACTS_CONTACT_DETAILS = "contacts_contact_details/{contactId}"

    fun contactDetail(contactId: String): String {
        return "contacts_contact_details/$contactId"
    }
    const val CONTACTS_EDIT_CONTACT_NAME = "contacts_edit_contact_name/{contactId}"
    fun editContactName(contactId: String): String {
        return "contacts_edit_contact_name/$contactId"
    }
    const val CONTACTS_EDIT_CONTACT_NOTE = "contacts_edit_contact_note/{contactId}"
    fun editContactNote(contactId: String): String {
        return "contacts_edit_contact_note/$contactId"
    }

    const val NOTIFICATIONS = "notifications"

    const val SETTINGS = "settings"
    const val SETTINGS_CHANGE_PASSWORD = "settings-change-password"
    const val SETTINGS_DELETE_ACCOUNT = "settings-delete-account"
}