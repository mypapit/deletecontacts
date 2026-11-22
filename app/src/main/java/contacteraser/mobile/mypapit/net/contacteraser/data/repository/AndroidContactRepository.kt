package contacteraser.mobile.mypapit.net.contacteraser.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import contacteraser.mobile.mypapit.net.contacteraser.R
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionProgress
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [ContactDataSource] implementation backed by Android's [ContactsContract] content provider.
 */
class AndroidContactRepository(
    context: Context
) : ContactDataSource {

    private val appContext = context.applicationContext
    private val resolver: ContentResolver = appContext.contentResolver

    override suspend fun getContactSummary(): ContactSummary = withContext(Dispatchers.IO) {
        val total = queryCount(withoutPhoneOnly = false)
        val withoutPhone = queryCount(withoutPhoneOnly = true)
        ContactSummary(
            totalContacts = total,
            contactsWithoutPhone = withoutPhone
        )
    }

    override suspend fun getContacts(filter: ContactFilter): List<ContactInfo> =
        withContext(Dispatchers.IO) {
            val withoutPhoneOnly = filter == ContactFilter.WITHOUT_PHONE
            val projection = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            )
            val selection = if (withoutPhoneOnly) {
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?"
            } else {
                null
            }
            val selectionArgs = if (withoutPhoneOnly) arrayOf("0") else null
            val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} COLLATE LOCALIZED ASC"

            val primaryPhones = if (withoutPhoneOnly) emptyMap() else queryPrimaryPhones()
            val primaryEmails = queryPrimaryEmails()
            val unnamedLabel = appContext.getString(R.string.unnamed_contact)
            val unavailableLabel = appContext.getString(R.string.not_available)

            val contacts = mutableListOf<ContactInfo>()
            resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                val lookupIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY)
                val phoneIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val rawName = cursor.getString(nameIndex)
                    val rawLookup = cursor.getString(lookupIndex)
                    val hasPhone = cursor.getInt(phoneIndex) > 0

                    contacts.add(
                        ContactInfo(
                            id = id,
                            displayName = if (rawName.isNullOrBlank()) unnamedLabel else rawName,
                            lookupKey = if (rawLookup.isNullOrBlank()) unavailableLabel else rawLookup,
                            hasPhoneNumber = hasPhone,
                            primaryPhone = primaryPhones[id],
                            primaryEmail = primaryEmails[id]
                        )
                    )
                }
            }
            contacts
        }

    override suspend fun deleteContacts(
        contacts: List<ContactInfo>,
        onProgress: (DeletionProgress) -> Unit
    ): DeletionResult = withContext(Dispatchers.IO) {
        var deletedCount = 0
        val total = contacts.size

        contacts.forEachIndexed { index, contact ->
            onProgress(
                DeletionProgress(
                    processed = index + 1,
                    total = total,
                    currentName = contact.displayName
                )
            )
            val deletedRawRows = resolver.delete(
                ContactsContract.RawContacts.CONTENT_URI,
                "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                arrayOf(contact.id.toString())
            )
            if (deletedRawRows > 0) {
                deletedCount++
            } else {
                // Fallback to lookup URI in case raw contact rows were already detached or aggregated.
                val lookupUri = ContactsContract.Contacts.getLookupUri(contact.id, contact.lookupKey)
                if (lookupUri != null && resolver.delete(lookupUri, null, null) > 0) {
                    deletedCount++
                }
            }
        }

        DeletionResult(
            deletedCount = deletedCount,
            requestedCount = total
        )
    }

    private fun queryCount(withoutPhoneOnly: Boolean): Int {
        val selection = if (withoutPhoneOnly) {
            "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = ?"
        } else {
            null
        }
        val selectionArgs = if (withoutPhoneOnly) arrayOf("0") else null
        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.count
        }
        return 0
    }

    private fun queryPrimaryPhones(): Map<Long, String> {
        val phones = mutableMapOf<Long, String>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val number = cursor.getString(numberIndex)?.trim()
                if (!number.isNullOrEmpty() && !phones.containsKey(id)) {
                    phones[id] = number
                }
            }
        }
        return phones
    }

    private fun queryPrimaryEmails(): Map<Long, String> {
        val emails = mutableMapOf<Long, String>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS
        )
        resolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val addressIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val address = cursor.getString(addressIndex)?.trim()
                if (!address.isNullOrEmpty() && !emails.containsKey(id)) {
                    emails[id] = address
                }
            }
        }
        return emails
    }
}
