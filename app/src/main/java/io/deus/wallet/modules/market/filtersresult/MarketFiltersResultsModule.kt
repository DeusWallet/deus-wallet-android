package io.deus.wallet.modules.market.filtersresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.filters.IMarketListFetcher

object MarketFiltersResultsModule {
    class Factory(val service: IMarketListFetcher) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = MarketFiltersResultService(service, App.marketFavoritesManager)
            return MarketFiltersResultViewModel(service) as T
        }

    }
}
