package io.deus.wallet.modules.market.favorites

import io.deus.wallet.core.ILocalStorage
import io.deus.wallet.modules.market.favorites.MarketFavoritesModule.Period
import io.deus.wallet.widgets.MarketWidgetManager

class MarketFavoritesMenuService(
    private val localStorage: ILocalStorage,
    private val marketWidgetManager: MarketWidgetManager
) {

    var sortDescending: Boolean
        get() = localStorage.marketFavoritesSortDescending
        set(value) {
            localStorage.marketFavoritesSortDescending = value
            marketWidgetManager.updateWatchListWidgets()
        }

    var period: Period
        get() = localStorage.marketFavoritesPeriod ?: Period.OneDay
        set(value) {
            localStorage.marketFavoritesPeriod = value
            marketWidgetManager.updateWatchListWidgets()
        }
}
