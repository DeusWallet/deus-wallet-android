package io.deus.wallet.modules.evmnetwork.addrpc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.horizontalsystems.marketkit.models.Blockchain

object AddRpcModule {

    class Factory(private val blockchain: Blockchain) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddRpcViewModel(blockchain, App.evmSyncSourceManager) as T
        }
    }

}
