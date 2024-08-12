package io.deus.wallet.modules.market.metricspage

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.chart.ChartCurrencyValueFormatterShortened
import io.deus.wallet.modules.chart.ChartModule
import io.deus.wallet.modules.chart.ChartViewModel
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.modules.market.tvl.GlobalMarketRepository
import io.deus.wallet.modules.metricchart.MetricsType
import io.deus.wallet.ui.compose.Select

object MetricsPageModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val metricsType: MetricsType) : ViewModelProvider.Factory {
        private val globalMarketRepository by lazy {
            GlobalMarketRepository(App.marketKit)
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MetricsPageViewModel::class.java -> {
                    val service = MetricsPageService(metricsType, App.currencyManager, globalMarketRepository)
                    MetricsPageViewModel(service) as T
                }
                ChartViewModel::class.java -> {
                    val chartService = MetricsPageChartService(App.currencyManager, metricsType, globalMarketRepository)
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    @Immutable
    data class MarketData(
        val menu: Menu,
        val marketViewItems: List<MarketViewItem>
    )

    @Immutable
    data class Menu(
        val sortDescending: Boolean,
        val marketFieldSelect: Select<MarketField>
    )
}

