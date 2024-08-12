package io.deus.wallet.modules.coin.coinmarkets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.DataState
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.MarketTickerViewItem
import io.deus.wallet.ui.compose.TranslatableString
import io.reactivex.disposables.CompositeDisposable

class CoinMarketsViewModel(private val service: CoinMarketsService) : ViewModel() {
    val verifiedMenu by service::verifiedMenu
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val viewItemsLiveData = MutableLiveData<List<MarketTickerViewItem>>()

    private val disposables = CompositeDisposable()

    init {
        service.stateObservable
            .subscribeIO {
                syncState(it)
            }
            .let {
                disposables.add(it)
            }

        service.start()
    }

    private fun syncState(state: DataState<List<MarketTickerItem>>) {
        viewStateLiveData.postValue(state.viewState)

        state.dataOrNull?.let { data ->
            viewItemsLiveData.postValue(data.map { createViewItem(it) })
        }
    }

    private fun createViewItem(item: MarketTickerItem): MarketTickerViewItem {
        return MarketTickerViewItem(
            item.market,
            item.marketImageUrl,
            "${item.baseCoinCode}/${item.targetCoinCode}",
            App.numberFormatter.formatFiatShort(item.volumeFiat, service.currency.symbol, service.currency.decimal),
            App.numberFormatter.formatCoinShort(item.volumeToken, item.baseCoinCode, 8),
            item.tradeUrl,
            if (item.verified) TranslatableString.ResString(R.string.CoinPage_MarketsLabel_Verified) else null
        )
    }

    override fun onCleared() {
        disposables.clear()
        service.stop()
    }

    fun onErrorClick() {
        service.start()
    }

    fun toggleVerifiedType(verifiedType: VerifiedType) {
        service.setVerifiedType(verifiedType)
    }

}
