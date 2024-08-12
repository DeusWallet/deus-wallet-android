package io.deus.wallet.modules.market.topnftcollections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TimeDuration
import io.deus.wallet.ui.compose.Select
import io.horizontalsystems.marketkit.models.BlockchainType
import java.math.BigDecimal

object TopNftCollectionsModule {

    class Factory(val sortingField: SortingField, val timeDuration: TimeDuration) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topNftCollectionsRepository = TopNftCollectionsRepository(App.marketKit)
            val service = TopNftCollectionsService(sortingField, timeDuration, topNftCollectionsRepository)
            val topNftCollectionsViewItemFactory = TopNftCollectionsViewItemFactory(App.numberFormatter)
            return TopNftCollectionsViewModel(service, topNftCollectionsViewItemFactory) as T
        }
    }

}

data class Menu(
    val sortingFieldSelect: Select<SortingField>,
    val timeDurationSelect: Select<TimeDuration>
)

data class TopNftCollectionViewItem(
    val blockchainType: BlockchainType,
    val uid: String,
    val name: String,
    val imageUrl: String?,
    val volume: String,
    val volumeDiff: BigDecimal,
    val order: Int,
    val floorPrice: String
)
