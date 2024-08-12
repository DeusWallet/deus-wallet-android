package io.deus.wallet.modules.contacts.viewmodel

import androidx.lifecycle.viewModelScope
import io.deus.wallet.R
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.shorten
import io.deus.wallet.modules.contacts.ContactsRepository
import io.deus.wallet.modules.contacts.Mode
import io.deus.wallet.modules.contacts.model.Contact
import io.deus.wallet.ui.compose.TranslatableString
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactsRepository,
    private val mode: Mode
) : ViewModelUiState<ContactsViewModel.UiState>() {

    private val readOnly = mode != Mode.Full
    private val showAddContact = !readOnly
    private val showMoreOptions = !readOnly

    private var nameQuery: String = ""
    private val contacts: List<Contact>
        get() = repository.getContactsFiltered(nameQuery = nameQuery)

    val backupJson: String
        get() = repository.asJsonString

    val backupFileName: String
        get() = "UW_Contacts_${System.currentTimeMillis() / 1000}.json"

    init {
        viewModelScope.launch {
            repository.contactsFlow.collect {
                emitState()
            }
        }
    }

    override fun createState() = UiState(
        contacts = contacts,
        nameQuery = nameQuery,
        searchMode = nameQuery.isNotEmpty(),
        showAddContact = showAddContact,
        showMoreOptions = showMoreOptions
    )

    fun onEnterQuery(query: String) {
        nameQuery = query
        emitState()
    }

    fun restore(json: String) {
        repository.restore(json)
    }

    fun shouldShowReplaceWarning(contact: Contact): Boolean {
        return mode is Mode.AddAddressToExistingContact && contact.addresses.any { it.blockchain.type == mode.blockchainType }
    }

    fun shouldShowRestoreWarning(): Boolean {
        return contacts.isNotEmpty()
    }

    fun replaceWarningMessage(contact: Contact): TranslatableString? {
        val blockchainType =
            (mode as? Mode.AddAddressToExistingContact)?.blockchainType ?: return null
        val address = (mode as? Mode.AddAddressToExistingContact)?.address ?: return null
        val oldAddress =
            contact.addresses.find { it.blockchain.type == blockchainType } ?: return null

        return TranslatableString.ResString(
            R.string.Contacts_AddAddress_ReplaceWarning,
            oldAddress.blockchain.name,
            oldAddress.address.shorten(),
            address.shorten()
        )
    }

    data class UiState(
        val contacts: List<Contact>,
        val nameQuery: String?,
        val searchMode: Boolean,
        val showAddContact: Boolean,
        val showMoreOptions: Boolean
    )

}
