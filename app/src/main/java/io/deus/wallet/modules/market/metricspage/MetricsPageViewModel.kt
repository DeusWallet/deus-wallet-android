package io.deus.wallet.modules.market.metricspage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.core.stats.statField
import io.deus.wallet.core.stats.statPage
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.modules.market.MarketItem
import io.deus.wallet.modules.market.MarketModule
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.modules.metricchart.MetricsType
import io.deus.wallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MetricsPageViewModel(
    private val service: MetricsPageService,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val marketFields = MarketField.values().toList()
    private var marketField: MarketField
    private var marketItems: List<MarketItem> = listOf()
    private val metricsType: MetricsType = service.metricsType
    private val statPage: StatPage =  metricsType.statPage

    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val marketLiveData = MutableLiveData<MetricsPageModule.MarketData>()
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    var header = MarketModule.Header(
        title = Translator.getString(metricsType.title),
        description = Translator.getString(metricsType.description),
        icon = metricsType.headerIcon
    )

    init {
        marketField = when (metricsType) {
            MetricsType.Volume24h -> MarketField.Volume
            MetricsType.TotalMarketCap,
            MetricsType.DefiCap,
            MetricsType.BtcDominance,
            MetricsType.TvlInDefi -> MarketField.MarketCap
        }

        service.marketItemsObservable
            .subscribeIO { marketItemsDataState ->
                marketItemsDataState.viewState?.let {
                    viewStateLiveData.postValue(it)
                }

                marketItemsDataState?.dataOrNull?.let {
                    marketItems = it
                    syncMarketItems(it)
                }
            }
            .let { disposables.add(it) }

        service.start()
    }

    private fun syncMarketItems(marketItems: List<MarketItem>) {
        marketLiveData.postValue(marketData(marketItems))
    }

    private fun marketData(marketItems: List<MarketItem>): MetricsPageModule.MarketData {
        val menu = MetricsPageModule.Menu(service.sortDescending, Select(marketField, marketFields))
        val marketViewItems = marketItems.map { MarketViewItem.create(it, marketField) }
        return MetricsPageModule.MarketData(menu, marketViewItems)
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    fun onToggleSortType() {
        service.sortDescending = !service.sortDescending

        stat(page = statPage, event = StatEvent.ToggleSortDirection)
    }

    fun onSelectMarketField(marketField: MarketField) {
        this.marketField = marketField
        syncMarketItems(marketItems)

        stat(page = statPage, event = StatEvent.SwitchField(marketField.statField))
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()

        stat(page = statPage, event = StatEvent.Refresh)
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }
}
