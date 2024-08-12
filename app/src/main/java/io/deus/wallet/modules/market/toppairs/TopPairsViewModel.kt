package io.deus.wallet.modules.market.toppairs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.App
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.market.overview.TopPairViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class TopPairsViewModel(
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager,
) : ViewModelUiState<TopPairsUiState>() {
    private var isRefreshing = false
    private var items = listOf<TopPairViewItem>()
    private var viewState: ViewState = ViewState.Loading

    init {
        viewModelScope.launch {
            currencyManager.baseCurrencyUpdatedSignal.asFlow().collect {
                fetchItems()
                emitState()
            }
        }

        viewModelScope.launch {
            fetchItems()
            emitState()
        }
    }

    override fun createState() = TopPairsUiState(
        isRefreshing = isRefreshing,
        items = items,
        viewState = viewState
    )

    private suspend fun fetchItems() = withContext(Dispatchers.Default) {
        try {
            val topPairs = marketKit.topPairsSingle(currencyManager.baseCurrency.code, 1, 100).await()
            items = topPairs.map {
                TopPairViewItem.createFromTopPair(it, currencyManager.baseCurrency.symbol)
            }
            viewState = ViewState.Success
        } catch (e: Throwable) {
            viewState = ViewState.Error(e)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            emitState()

            fetchItems()
            delay(1000)
            isRefreshing = false
            emitState()
        }
    }

    fun onErrorClick() {
        refresh()
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TopPairsViewModel(App.marketKit, App.currencyManager) as T
        }
    }

}

data class TopPairsUiState(
    val isRefreshing: Boolean,
    val items: List<TopPairViewItem>,
    val viewState: ViewState
)
