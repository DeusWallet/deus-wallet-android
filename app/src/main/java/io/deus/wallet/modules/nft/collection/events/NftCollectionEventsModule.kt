package io.deus.wallet.modules.nft.collection.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.providers.nft.NftEventsProvider
import io.deus.wallet.entities.nft.NftEventMetadata
import io.deus.wallet.entities.nft.NftUid
import io.deus.wallet.modules.balance.BalanceXRateRepository
import io.deus.wallet.modules.coin.ContractInfo
import io.horizontalsystems.marketkit.models.BlockchainType

class NftCollectionEventsModule {

    class Factory(
        private val eventListType: NftEventListType,
        private val defaultEventType: NftEventMetadata.EventType = NftEventMetadata.EventType.Sale
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftCollectionEventsService(
                eventListType,
                defaultEventType,
                NftEventsProvider(App.marketKit),
                BalanceXRateRepository("nft-collection-events", App.currencyManager, App.marketKit)
            )
            return NftCollectionEventsViewModel(service) as T
        }
    }
}

enum class SelectorDialogState {
     Closed, Opened
}

sealed class NftEventListType {
    data class Collection(
        val blockchainType: BlockchainType,
        val providerUid: String,
        val contracts: List<ContractInfo>
    ) : NftEventListType()

    data class Asset(val nftUid: NftUid) : NftEventListType()
}
