package io.deus.wallet.modules.market.filtersresult

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.core.stats.statField
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.category.MarketItemWrapper
import io.deus.wallet.modules.market.topcoins.SelectorDialogState
import io.deus.wallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class MarketFiltersResultViewModel(
    private val service: MarketFiltersResultService,
) : ViewModel() {

    private var marketItems: List<MarketItemWrapper> = listOf()

    var viewState by mutableStateOf<ViewState>(ViewState.Loading)
        private set

    var viewItemsState by mutableStateOf<List<MarketViewItem>>(listOf())
        private set

    var selectorDialogState by mutableStateOf<SelectorDialogState>(SelectorDialogState.Closed)
        private set

    var menuState by mutableStateOf(service.menu)
        private set

    private val disposable = CompositeDisposable()

    init {
        syncMenu()

        service.stateObservable
            .subscribeIO {
                syncState(it)
            }
            .let {
                disposable.add(it)
            }

        service.start()
    }

    override fun onCleared() {
        service.stop()
        disposable.clear()
    }

    fun onErrorClick() {
        service.refresh()
    }

    fun showSelectorMenu() {
        selectorDialogState =
            SelectorDialogState.Opened(Select(service.sortingField, service.sortingFields))
    }

    fun onSelectorDialogDismiss() {
        selectorDialogState = SelectorDialogState.Closed
    }

    fun onSelectSortingField(sortingField: SortingField) {
        service.updateSortingField(sortingField)
        selectorDialogState = SelectorDialogState.Closed
        syncMenu()
    }

    fun marketFieldSelected(marketField: MarketField) {
        service.marketField = marketField

        syncMarketViewItems()
        syncMenu()

        stat(page = StatPage.AdvancedSearchResults, event = StatEvent.SwitchField(marketField.statField))
    }

    fun onAddFavorite(uid: String) {
        service.addFavorite(uid)
    }

    fun onRemoveFavorite(uid: String) {
        service.removeFavorite(uid)
    }

    private fun syncState(state: DataState<List<MarketItemWrapper>>) {
        viewModelScope.launch {
            state.viewState?.let {
                viewState = it
            }

            state.dataOrNull?.let {
                marketItems = it

                syncMarketViewItems()
            }

            syncMenu()
        }
    }

    private fun syncMenu() {
        menuState = service.menu
    }

    private fun syncMarketViewItems() {
        viewItemsState = marketItems.map {
            MarketViewItem.create(it.marketItem, service.marketField, it.favorited)
        }.toList()
    }

}
