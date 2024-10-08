package io.deus.wallet.modules.coin.treasuries

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.WithTranslatableTitle
import io.horizontalsystems.marketkit.models.Coin

object CoinTreasuriesModule {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val coin: Coin) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = CoinTreasuriesRepository(App.marketKit)
            val service = CoinTreasuriesService(coin, repository, App.currencyManager)
            return CoinTreasuriesViewModel(service, App.numberFormatter) as T
        }
    }

    @Immutable
    data class CoinTreasuriesData(
        val treasuryTypeSelect: Select<TreasuryTypeFilter>,
        val sortDescending: Boolean,
        val coinTreasuries: List<CoinTreasuryItem>
    )

    @Immutable
    data class CoinTreasuryItem(
        val fund: String,
        val fundLogoUrl: String,
        val country: String,
        val amount: String,
        val amountInCurrency: String
    )

    enum class TreasuryTypeFilter : WithTranslatableTitle {
        All, Public, Private, ETF;

        override val title: TranslatableString
            get() = when (this) {
                All -> TranslatableString.ResString(R.string.MarketGlobalMetrics_ChainSelectorAll)
                else -> TranslatableString.PlainString(name)
            }
    }

    sealed class SelectorDialogState {
        object Closed : SelectorDialogState()
        class Opened(val select: Select<TreasuryTypeFilter>) : SelectorDialogState()
    }
}
