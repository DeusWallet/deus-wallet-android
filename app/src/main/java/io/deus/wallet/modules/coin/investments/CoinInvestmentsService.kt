package io.deus.wallet.modules.coin.investments

import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.Currency
import io.deus.wallet.entities.DataState
import io.horizontalsystems.marketkit.models.CoinInvestment
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CoinInvestmentsService(
    private val coinUid: String,
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager
) {
    private var disposable: Disposable? = null

    private val stateSubject = BehaviorSubject.create<DataState<List<CoinInvestment>>>()
    val stateObservable: Observable<DataState<List<CoinInvestment>>>
        get() = stateSubject

    val usdCurrency: Currency
        get() {
            val currencies = currencyManager.currencies
            return currencies.first { it.code == "USD" }
        }

    private fun fetch() {
        disposable?.dispose()

        marketKit.investmentsSingle(coinUid)
            .subscribeIO({ coinInvestments ->
                stateSubject.onNext(DataState.Success(coinInvestments))
            }, { error ->
                stateSubject.onNext(DataState.Error(error))
            }).let { disposable = it }
    }

    fun start() {
        fetch()
    }

    fun refresh() {
        fetch()
    }

    fun stop() {
        disposable?.dispose()
    }
}
