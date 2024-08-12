package io.deus.wallet.modules.market.topcoins

import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.sort
import io.horizontalsystems.marketkit.models.TopMovers
import io.reactivex.Single
import java.lang.Integer.min

class MarketTopMoversRepository(
    private val marketKit: MarketKitWrapper
) {

    fun getTopMovers(baseCurrency: Currency): Single<TopMovers> =
        marketKit.topMoversSingle(baseCurrency.code)

    fun get(
        size: Int,
        sortingField: SortingField,
        limit: Int,
        baseCurrency: Currency,
        marketField: MarketField
    ): Single<List<MarketItem>> =
        Single.create { emitter ->
            try {
                val marketInfoList = marketKit.marketInfosSingle(size, baseCurrency.code, false).blockingGet()
                val marketItemList = marketInfoList.map { marketInfo ->
                    MarketItem.createFromCoinMarket(
                        marketInfo,
                        baseCurrency,
                    )
                }

                val sortedMarketItems = marketItemList
                    .subList(0, min(marketInfoList.size, size))
                    .sort(sortingField)
                    .subList(0, min(marketInfoList.size, limit))

                emitter.onSuccess(sortedMarketItems)
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }

}
