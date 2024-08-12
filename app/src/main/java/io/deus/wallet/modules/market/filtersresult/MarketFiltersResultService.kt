package io.deus.wallet.modules.market.filtersresult

import io.deus.wallet.core.managers.MarketFavoritesManager
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.category.MarketCategoryModule
import io.deus.wallet.modules.market.category.MarketItemWrapper
import io.deus.wallet.modules.market.filters.IMarketListFetcher
import io.deus.wallet.modules.market.sort
import io.deus.wallet.ui.compose.Select
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MarketFiltersResultService(
    private val fetcher: IMarketListFetcher,
    private val favoritesManager: MarketFavoritesManager,
) {
    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> =
        BehaviorSubject.create()

    var marketItems: List<MarketItem> = listOf()

    val sortingFields = SortingField.values().toList()
    private val marketFields = MarketField.values().toList()
    var sortingField = SortingField.HighestCap
    var marketField = MarketField.PriceDiff

    val menu: MarketCategoryModule.Menu
        get() = MarketCategoryModule.Menu(
            Select(sortingField, sortingFields),
            Select(marketField, marketFields)
        )

    private var fetchDisposable: Disposable? = null
    private var favoriteDisposable: Disposable? = null

    fun start() {
        fetch()

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                favoriteDisposable = it
            }
    }

    fun stop() {
        favoriteDisposable?.dispose()
        fetchDisposable?.dispose()
    }

    fun refresh() {
        fetch()
    }

    fun updateSortingField(sortingField: SortingField) {
        this.sortingField = sortingField
        syncItems()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }

    private fun fetch() {
        fetchDisposable?.dispose()

        fetcher.fetchAsync()
            .subscribeIO({
                marketItems = it
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                fetchDisposable = it
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }

        val items = marketItems
            .sort(sortingField)
            .map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }

        stateObservable.onNext(DataState.Success(items))
    }

}
