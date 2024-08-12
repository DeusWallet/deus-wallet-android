package io.deus.wallet.modules.coin.treasuries

import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.Currency
import io.deus.wallet.entities.DataState
import io.deus.wallet.modules.coin.treasuries.CoinTreasuriesModule.TreasuryTypeFilter
import io.horizontalsystems.marketkit.models.Coin
import io.horizontalsystems.marketkit.models.CoinTreasury
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CoinTreasuriesService(
    val coin: Coin,
    private val repository: CoinTreasuriesRepository,
    private val currencyManager: CurrencyManager
) {
    private var disposable: Disposable? = null

    private val stateSubject = BehaviorSubject.create<DataState<List<CoinTreasury>>>()
    val stateObservable: Observable<DataState<List<CoinTreasury>>>
        get() = stateSubject

    val currency: Currency
        get() = currencyManager.baseCurrency

    val treasuryTypes = TreasuryTypeFilter.values().toList()
    var treasuryType: TreasuryTypeFilter = TreasuryTypeFilter.All
        set(value) {
            field = value
            rebuildItems()
        }

    var sortDescending: Boolean = true
        set(value) {
            field = value
            rebuildItems()
        }

    private fun rebuildItems() {
        fetch(forceRefresh = false)
    }

    private fun forceRefresh() {
        fetch(forceRefresh = true)
    }

    private fun fetch(forceRefresh: Boolean) {
        disposable?.dispose()

        repository.coinTreasuriesSingle(coin.uid, currency.code, treasuryType, sortDescending, forceRefresh)
            .subscribeIO({
                stateSubject.onNext(DataState.Success(it))
            }, {
                stateSubject.onNext(DataState.Error(it))
            }).let { disposable = it }
    }

    fun start() {
        forceRefresh()
    }

    fun refresh() {
        forceRefresh()
    }

    fun stop() {
        disposable?.dispose()
    }
}
