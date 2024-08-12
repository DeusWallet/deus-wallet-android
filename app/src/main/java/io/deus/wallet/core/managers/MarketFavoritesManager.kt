package io.deus.wallet.core.managers

import io.deus.wallet.core.storage.AppDatabase
import io.deus.wallet.core.storage.FavoriteCoin
import io.deus.wallet.core.storage.MarketFavoritesDao
import io.deus.wallet.widgets.MarketWidgetManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MarketFavoritesManager(
    appDatabase: AppDatabase,
    private val marketWidgetManager: MarketWidgetManager
) {
    val dataUpdatedAsync: Observable<Unit>
        get() = dataUpdatedSubject

    private val dataUpdatedSubject = PublishSubject.create<Unit>()

    private val dao: MarketFavoritesDao by lazy {
        appDatabase.marketFavoritesDao()
    }

    fun add(coinUid: String) {
        dao.insert(FavoriteCoin(coinUid))
        dataUpdatedSubject.onNext(Unit)
        marketWidgetManager.updateWatchListWidgets()
    }

    fun addAll(coinUids: List<String>) {
        dao.insertAll(coinUids.map { FavoriteCoin(it) })
        dataUpdatedSubject.onNext(Unit)
        marketWidgetManager.updateWatchListWidgets()
    }

    fun remove(coinUid: String) {
        dao.delete(coinUid)
        dataUpdatedSubject.onNext(Unit)
        marketWidgetManager.updateWatchListWidgets()
    }

    fun getAll(): List<FavoriteCoin> {
        return dao.getAll()
    }

    fun isCoinInFavorites(coinUid: String): Boolean {
        return dao.getCount(coinUid) > 0
    }
}
