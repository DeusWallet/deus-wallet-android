package io.deus.wallet.modules.coin.investments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.IAppNumberFormatter
import io.deus.wallet.core.logoUrl
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.investments.CoinInvestmentsModule.FundViewItem
import io.deus.wallet.modules.coin.investments.CoinInvestmentsModule.ViewItem
import io.horizontalsystems.core.helpers.DateHelper
import io.horizontalsystems.marketkit.models.CoinInvestment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoinInvestmentsViewModel(
    private val service: CoinInvestmentsService,
    private val numberFormatter: IAppNumberFormatter
) : ViewModel() {
    private val disposables = CompositeDisposable()

    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val viewItemsLiveData = MutableLiveData<List<ViewItem>>()

    init {
        service.stateObservable
            .subscribeIO({ state ->
                when (state) {
                    is DataState.Success -> {
                        viewStateLiveData.postValue(ViewState.Success)

                        sync(state.data)
                    }
                    is DataState.Error -> {
                        viewStateLiveData.postValue(ViewState.Error(state.error))
                    }
                    DataState.Loading -> {}
                }
            }, {
                viewStateLiveData.postValue(ViewState.Error(it))
            }).let {
                disposables.add(it)
            }

        service.start()
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        disposables.clear()
        service.stop()
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    private fun sync(investments: List<CoinInvestment>) {
        viewItemsLiveData.postValue(investments.map { viewItem(it) })
    }

    private fun viewItem(investment: CoinInvestment): ViewItem {
        val amount = investment.amount?.let {
            numberFormatter.formatFiatShort(it, service.usdCurrency.symbol, 2)
        } ?: "---"
        val dateString = DateHelper.formatDate(investment.date, "MMM dd, yyyy")

        return ViewItem(
            amount = amount,
            info = "${investment.round} - $dateString",
            fundViewItems = investment.funds.map {
                FundViewItem(it.name, it.logoUrl, it.isLead, it.website)
            }
        )
    }
}
