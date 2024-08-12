package io.deus.wallet.modules.coin.majorholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.CoinViewFactory
import io.deus.wallet.modules.coin.MajorHolderItem
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.StackBarSlice
import io.horizontalsystems.marketkit.models.Blockchain

object CoinMajorHoldersModule {
    class Factory(private val coinUid: String, private val blockchain: Blockchain) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val factory = CoinViewFactory(App.currencyManager.baseCurrency, App.numberFormatter)
            return CoinMajorHoldersViewModel(coinUid, blockchain, App.marketKit, factory) as T
        }
    }

    data class UiState(
        val viewState: ViewState,
        val top10Share: String,
        val totalHoldersCount: String,
        val seeAllUrl: String?,
        val chartData: List<StackBarSlice>,
        val topHolders: List<MajorHolderItem>,
        val error: TranslatableString?,
    )
}
