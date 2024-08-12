package io.deus.wallet.modules.market.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.chart.ChartCurrencyValueFormatterShortened
import io.deus.wallet.modules.chart.ChartModule
import io.deus.wallet.modules.chart.ChartViewModel
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TopMarket
import io.deus.wallet.ui.compose.Select
import io.horizontalsystems.marketkit.models.CoinCategory

object MarketCategoryModule {

    class Factory(
        private val coinCategory: CoinCategory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MarketCategoryViewModel::class.java -> {
                    val marketCategoryRepository = MarketCategoryRepository(App.marketKit)
                    val service = MarketCategoryService(
                        marketCategoryRepository,
                        App.currencyManager,
                        App.languageManager,
                        App.marketFavoritesManager,
                        coinCategory,
                        defaultTopMarket,
                        defaultSortingField
                    )
                    MarketCategoryViewModel(service) as T
                }

                ChartViewModel::class.java -> {
                    val chartService = CoinCategoryMarketDataChartService(
                        App.currencyManager,
                        App.marketKit,
                        coinCategory.uid
                    )
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

        companion object {
            val defaultSortingField = SortingField.HighestCap
            val defaultTopMarket = TopMarket.Top100
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<SortingField>,
        val marketFieldSelect: Select<MarketField>
    )

}

data class MarketItemWrapper(
    val marketItem: MarketItem,
    val favorited: Boolean,
)