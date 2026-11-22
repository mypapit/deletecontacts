package contacteraser.mobile.mypapit.net.contacteraser.data.model

/**
 * Represents a device contact entry retrieved from Android ContactsContract.
 */
data class ContactInfo(
    val id: Long,
    val displayName: String,
    val lookupKey: String,
    val hasPhoneNumber: Boolean,
    val primaryPhone: String? = null,
    val primaryEmail: String? = null
)

/**
 * Aggregated counts shown on the Overview dashboard.
 */
data class ContactSummary(
    val totalContacts: Int,
    val contactsWithoutPhone: Int
)

/**
 * Filter modes for browsing contacts in the Contacts tab.
 */
enum class ContactFilter {
    WITHOUT_PHONE,
    ALL
}

/**
 * User-selectable appearance modes persisted in DataStore Preferences.
 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Live progress emitted while deleting contacts in a background batch.
 */
data class DeletionProgress(
    val processed: Int,
    val total: Int,
    val currentName: String
)

/**
 * Outcome returned after completing a contact deletion batch.
 */
data class DeletionResult(
    val deletedCount: Int,
    val requestedCount: Int
)
