package io.deus.wallet.modules.market

import io.deus.wallet.core.ILocalStorage
import io.deus.wallet.core.IMarketStorage
import io.deus.wallet.entities.LaunchPage

class MarketService(
    private val storage: IMarketStorage,
    private val localStorage: ILocalStorage,
) {
    val launchPage: LaunchPage?
        get() = localStorage.launchPage

    var currentTab: MarketModule.Tab?
        get() = storage.currentMarketTab
        set(value) {
            storage.currentMarketTab = value
        }

}
