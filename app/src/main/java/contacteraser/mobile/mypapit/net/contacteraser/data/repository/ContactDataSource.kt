package contacteraser.mobile.mypapit.net.contacteraser.data.repository

import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactFilter
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactInfo
import contacteraser.mobile.mypapit.net.contacteraser.data.model.ContactSummary
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionProgress
import contacteraser.mobile.mypapit.net.contacteraser.data.model.DeletionResult

/**
 * Contract for reading and deleting contacts from the backing contact store.
 */
interface ContactDataSource {
    suspend fun getContactSummary(): ContactSummary
    suspend fun getContacts(filter: ContactFilter): List<ContactInfo>
    suspend fun deleteContacts(
        contacts: List<ContactInfo>,
        onProgress: (DeletionProgress) -> Unit = {}
    ): DeletionResult
}
