package io.deus.wallet.modules.market.topcoins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TopMarket
import io.deus.wallet.ui.compose.Select

object MarketTopCoinsModule {

    class Factory(
        private val topMarket: TopMarket? = null,
        private val sortingField: SortingField? = null,
        private val marketField: MarketField? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topMarketsRepository = MarketTopMoversRepository(App.marketKit)
            val service = MarketTopCoinsService(
                topMarketsRepository,
                App.currencyManager,
                App.marketFavoritesManager,
                topMarket ?: defaultTopMarket,
                sortingField ?: defaultSortingField,
                marketField ?: defaultMarketField
            )
            return MarketTopCoinsViewModel(
                service,
                marketField ?: defaultMarketField
            ) as T
        }

        companion object {
            val defaultSortingField = SortingField.HighestCap
            val defaultTopMarket = TopMarket.Top100
            val defaultMarketField = MarketField.PriceDiff
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<SortingField>,
        val topMarketSelect: Select<TopMarket>?,
        val marketFieldSelect: Select<MarketField>
    )

}

sealed class SelectorDialogState() {
    object Closed : SelectorDialogState()
    class Opened(val select: Select<SortingField>) : SelectorDialogState()
}
