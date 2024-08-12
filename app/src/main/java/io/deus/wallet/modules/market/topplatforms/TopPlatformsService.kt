package io.deus.wallet.modules.market.topplatforms

import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TimeDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopPlatformsService(
    private val repository: TopPlatformsRepository,
    private val currencyManager: CurrencyManager,
) {

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    suspend fun getTopPlatforms(
        sortingField: SortingField,
        timeDuration: TimeDuration,
        forceRefresh: Boolean
    ): List<TopPlatformItem> = withContext(Dispatchers.IO) {
        repository.get(sortingField, timeDuration, forceRefresh)
    }

}
