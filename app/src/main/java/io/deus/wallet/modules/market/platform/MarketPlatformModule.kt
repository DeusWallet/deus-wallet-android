package io.deus.wallet.modules.market.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.chart.ChartCurrencyValueFormatterShortened
import io.deus.wallet.modules.chart.ChartModule
import io.deus.wallet.modules.chart.ChartViewModel
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.topplatforms.Platform
import io.deus.wallet.ui.compose.Select

object MarketPlatformModule {

    class Factory(private val platform: Platform) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MarketPlatformViewModel::class.java -> {
                    val repository =
                        MarketPlatformCoinsRepository(platform, App.marketKit, App.currencyManager)
                    MarketPlatformViewModel(platform, repository, App.marketFavoritesManager) as T
                }

                ChartViewModel::class.java -> {
                    val chartService =
                        PlatformChartService(platform, App.currencyManager, App.marketKit)
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

    }

    data class Menu(
        val sortingFieldSelect: Select<SortingField>,
        val marketFieldSelect: Select<MarketField>
    )

}
