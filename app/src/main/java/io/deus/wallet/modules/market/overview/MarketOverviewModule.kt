package io.deus.wallet.modules.market.overview

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.MarketModule
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.modules.market.TimeDuration
import io.deus.wallet.modules.market.TopMarket
import io.deus.wallet.modules.market.search.MarketSearchModule.DiscoveryItem.Category
import io.deus.wallet.modules.market.topcoins.MarketTopMoversRepository
import io.deus.wallet.modules.market.topnftcollections.TopNftCollectionViewItem
import io.deus.wallet.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import io.deus.wallet.modules.market.topplatforms.TopPlatformViewItem
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.extensions.MetricData
import io.horizontalsystems.marketkit.models.TopPair
import java.math.BigDecimal

object MarketOverviewModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topMarketsRepository = MarketTopMoversRepository(App.marketKit)
            val service = MarketOverviewService(
                topMarketsRepository,
                App.marketKit,
                App.backgroundManager,
                App.currencyManager
            )
            val topNftCollectionsViewItemFactory = TopNftCollectionsViewItemFactory(App.numberFormatter)
            return MarketOverviewViewModel(service, topNftCollectionsViewItemFactory, App.currencyManager) as T
        }
    }

    @Immutable
    data class ViewItem(
        val marketMetrics: MarketMetrics,
        val boards: List<Board>,
        val topNftCollectionsBoard: TopNftCollectionsBoard,
        val topSectorsBoard: TopSectorsBoard,
        val topPlatformsBoard: TopPlatformsBoard,
        val topMarketPairs: List<TopPairViewItem>,
    )

    data class MarketMetrics(
        val totalMarketCap: MetricData,
        val volume24h: MetricData,
        val defiCap: MetricData,
        val defiTvl: MetricData,
    ) {
        operator fun get(page: Int) = when (page) {
            0 -> totalMarketCap
            1 -> volume24h
            2 -> defiCap
            3 -> defiTvl
            else -> throw  IndexOutOfBoundsException()
        }
    }

    data class MarketMetricsPoint(
        val value: BigDecimal,
        val timestamp: Long
    )

    data class Board(
        val boardHeader: BoardHeader,
        val marketViewItems: List<MarketViewItem>,
        val type: MarketModule.ListType
    )

    data class BoardHeader(
        val title: Int,
        val iconRes: Int,
        val topMarketSelect: Select<TopMarket>
    )

    data class TopNftCollectionsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<TimeDuration>,
        val collections: List<TopNftCollectionViewItem>
    )

    data class TopSectorsBoard(
        val title: Int,
        val iconRes: Int,
        val items: List<Category>
    )

    data class TopPlatformsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<TimeDuration>,
        val items: List<TopPlatformViewItem>
    )

}

data class TopPairViewItem(
    val title: String,
    val rank: String,
    val name: String,
    val iconUrl: String?,
    val tradeUrl: String?,
    val volume: String,
    val price: String?
) {
    companion object {
        fun createFromTopPair(topPair: TopPair, currencySymbol: String): TopPairViewItem {
            val volumeStr = App.numberFormatter.formatFiatShort(topPair.volume, currencySymbol, 2)

            val priceStr = topPair.price?.let {
                App.numberFormatter.formatCoinShort(
                    it,
                    topPair.target,
                    8
                )
            }

            return TopPairViewItem(
                title = "${topPair.base}/${topPair.target}",
                name = topPair.marketName ?: "",
                iconUrl = topPair.marketLogo,
                rank = topPair.rank.toString(),
                tradeUrl = topPair.tradeUrl,
                volume = volumeStr,
                price = priceStr,
            )
        }
    }
}
