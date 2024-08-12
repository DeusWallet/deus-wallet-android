package io.deus.wallet.modules.market.category

import io.deus.wallet.core.imageUrl
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.LanguageManager
import io.deus.wallet.core.managers.MarketFavoritesManager
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TopMarket
import io.horizontalsystems.marketkit.models.CoinCategory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MarketCategoryService(
    private val marketCategoryRepository: MarketCategoryRepository,
    private val currencyManager: CurrencyManager,
    private val languageManager: LanguageManager,
    private val favoritesManager: MarketFavoritesManager,
    private val coinCategory: CoinCategory,
    topMarket: TopMarket = TopMarket.Top100,
    sortingField: SortingField = SortingField.HighestCap,
) {
    private var disposables = CompositeDisposable()
    private var favoriteDisposables = CompositeDisposable()

    private var marketItems: List<MarketItem> = listOf()

    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> = BehaviorSubject.create()

    var topMarket: TopMarket = topMarket
        private set

    val sortingFields = SortingField.values().toList()
    var sortingField: SortingField = sortingField
        private set

    val coinCategoryName: String get() = coinCategory.name
    val coinCategoryDescription: String get() = coinCategory.description[languageManager.currentLocaleTag]
        ?: coinCategory.description["en"]
        ?: coinCategory.description.keys.firstOrNull()
        ?: ""
    val coinCategoryImageUrl: String get() = coinCategory.imageUrl

    fun setSortingField(sortingField: SortingField) {
        this.sortingField = sortingField
        sync(false)
    }

    private fun sync(forceRefresh: Boolean) {
        disposables.clear()

        marketCategoryRepository
            .get(
                coinCategory.uid,
                topMarket.value,
                sortingField,
                topMarket.value,
                currencyManager.baseCurrency,
                forceRefresh
            )
            .subscribeIO({ items ->
                marketItems = items
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                disposables.add(it)
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }
        val items = marketItems.map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }
        stateObservable.onNext(DataState.Success(items))
    }

    fun start() {
        sync(true)

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                favoriteDisposables.add(it)
            }
    }

    fun refresh() {
        sync(true)
    }

    fun stop() {
        disposables.clear()
        favoriteDisposables.clear()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }
}
