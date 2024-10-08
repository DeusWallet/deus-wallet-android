package io.deus.wallet.modules.market.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.modules.market.category.MarketItemWrapper
import io.deus.wallet.modules.market.favorites.MarketFavoritesModule.Period
import io.deus.wallet.modules.market.favorites.MarketFavoritesModule.ViewItem
import io.deus.wallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MarketFavoritesViewModel(
    private val service: MarketFavoritesService,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private var marketItemsWrapper: List<MarketItemWrapper> = listOf()
    private val timeDurationOptions: List<Period> = listOf(
        Period.OneDay,
        Period.SevenDay,
        Period.ThirtyDay,
    )

    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val viewItemLiveData = MutableLiveData<ViewItem>()

    init {
        service.marketItemsObservable
            .subscribeIO { state ->
                when (state) {
                    is DataState.Success -> {
                        viewStateLiveData.postValue(ViewState.Success)
                        marketItemsWrapper = state.data
                        syncViewItem()
                    }

                    is DataState.Error -> {
                        viewStateLiveData.postValue(ViewState.Error(state.error))
                    }

                    DataState.Loading -> {}
                }
            }.let { disposables.add(it) }

        service.start()
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    private fun syncViewItem() {
        viewItemLiveData.postValue(
            ViewItem(
                sortingDescending = service.sortDescending,
                periodSelect = Select(service.period, timeDurationOptions),
                marketItems = marketItemsWrapper.map {
                    MarketViewItem.create(it.marketItem, true)
                }
            )
        )
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onSortToggle() {
        service.sortDescending = !service.sortDescending
    }

    fun onSelectTimeDuration(period: Period) {
        service.period = period
    }

    override fun onCleared() {
        disposables.clear()
        service.stop()
    }

    fun removeFromFavorites(uid: String) {
        service.removeFavorite(uid)
    }
}
