package io.deus.wallet.modules.unlinkaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.entities.Account

object UnlinkAccountModule {
    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UnlinkAccountViewModel(account, App.accountManager) as T
        }
    }
}