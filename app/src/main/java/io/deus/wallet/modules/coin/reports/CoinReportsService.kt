package io.deus.wallet.modules.coin.reports

import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.horizontalsystems.marketkit.models.CoinReport
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CoinReportsService(
    private val coinUid: String,
    private val marketKit: MarketKitWrapper
) {
    private var disposable: Disposable? = null

    private val stateSubject = BehaviorSubject.create<DataState<List<CoinReport>>>()
    val stateObservable: Observable<DataState<List<CoinReport>>>
        get() = stateSubject

    private fun fetch() {
        disposable?.dispose()

        marketKit.coinReportsSingle(coinUid)
            .subscribeIO({ reports ->
                stateSubject.onNext(DataState.Success(reports))
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
