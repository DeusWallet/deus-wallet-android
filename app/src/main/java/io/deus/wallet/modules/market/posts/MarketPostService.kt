package io.deus.wallet.modules.market.posts

import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.entities.DataState
import io.horizontalsystems.core.BackgroundManager
import io.horizontalsystems.marketkit.models.Post
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class MarketPostService(
    private val marketKit: MarketKitWrapper,
    private val backgroundManager: BackgroundManager,
) : BackgroundManager.Listener {

    private var disposable: Disposable? = null

    private val stateSubject = BehaviorSubject.create<DataState<List<Post>>>()
    val stateObservable: Observable<DataState<List<Post>>>
        get() = stateSubject

    init {
        backgroundManager.registerListener(this)
        fetchPosts()
    }

    private fun fetchPosts() {
        disposable?.dispose()
        disposable = marketKit.postsSingle()
            .subscribeOn(Schedulers.io())
            .subscribe({
                stateSubject.onNext(DataState.Success(it))
            }, {
                stateSubject.onNext(DataState.Error(it))
            })
    }

    override fun willEnterForeground() {
        fetchPosts()
    }

    fun start() {
        fetchPosts()
    }

    fun stop() {
        disposable?.dispose()
        backgroundManager.unregisterListener(this)
    }

    fun refresh() {
        fetchPosts()
    }
}
