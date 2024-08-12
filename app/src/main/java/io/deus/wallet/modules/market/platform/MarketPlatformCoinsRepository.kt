package io.deus.wallet.modules.market.platform

import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.sort
import io.deus.wallet.modules.market.topplatforms.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MarketPlatformCoinsRepository(
    private val platform: Platform,
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager
) {
    private var itemsCache: List<MarketItem>? = null

    suspend fun get(
        sortingField: SortingField,
        forceRefresh: Boolean,
        limit: Int? = null,
    ) = withContext(Dispatchers.IO) {
        val currentCache = itemsCache

        val items = if (forceRefresh || currentCache == null) {
            val marketInfoItems = marketKit
                .topPlatformCoinListSingle(platform.uid, currencyManager.baseCurrency.code)
                .await()

            marketInfoItems.map { marketInfo ->
                MarketItem.createFromCoinMarket(marketInfo, currencyManager.baseCurrency)
            }
        } else {
            currentCache
        }

        itemsCache = items

        itemsCache?.sort(sortingField)?.let { sortedList ->
            limit?.let { sortedList.take(it) } ?: sortedList
        }
    }

}
