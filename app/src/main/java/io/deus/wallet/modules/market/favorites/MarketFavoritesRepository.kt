package io.deus.wallet.modules.market.favorites

import io.deus.wallet.core.managers.MarketFavoritesManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.favorites.MarketFavoritesModule.Period
import io.deus.wallet.modules.market.sort
import io.reactivex.Single

class MarketFavoritesRepository(
    private val marketKit: MarketKitWrapper,
    private val manager: MarketFavoritesManager
) {
    val dataUpdatedObservable by manager::dataUpdatedAsync

    private fun getFavorites(
        currency: Currency,
        period: Period
    ): List<MarketItem> {
        val favoriteCoins = manager.getAll()
        var marketItems = listOf<MarketItem>()
        if (favoriteCoins.isNotEmpty()) {
            val favoriteCoinUids = favoriteCoins.map { it.coinUid }
            marketItems = marketKit.marketInfosSingle(favoriteCoinUids, currency.code).blockingGet()
                .map { marketInfo ->
                    MarketItem.createFromCoinMarket(
                        marketInfo = marketInfo,
                        currency = currency,
                        period = period
                    )
                }
        }
        return marketItems
    }

    fun get(
        sortDescending: Boolean,
        period: Period,
        currency: Currency,
    ): Single<List<MarketItem>> =
        Single.create { emitter ->
            val sortingField = if (sortDescending) SortingField.TopGainers else SortingField.TopLosers
            try {
                val marketItems = getFavorites(currency, period)
                emitter.onSuccess(
                    marketItems.sort(sortingField)
                )
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }

    fun removeFavorite(uid: String) {
        manager.remove(uid)
    }
}
