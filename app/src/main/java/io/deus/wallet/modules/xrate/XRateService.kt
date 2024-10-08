package io.deus.wallet.modules.xrate

import androidx.lifecycle.ViewModel
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.entities.Currency
import io.deus.wallet.entities.CurrencyValue
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow

class XRateService(
    private val marketKit: MarketKitWrapper,
    private val currency: Currency
) : ViewModel() {

    fun getRate(coinUid: String): CurrencyValue? {
        return marketKit.coinPrice(coinUid, currency.code)?.let {
            CurrencyValue(currency, it.value)
        }
    }

    fun getRateFlow(coinUid: String): Flow<CurrencyValue> {
        return marketKit.coinPriceObservable("xrate-service", coinUid, currency.code)
            .subscribeOn(Schedulers.io())
            .map {
                CurrencyValue(currency, it.value)
            }
            .asFlow()
    }
}
