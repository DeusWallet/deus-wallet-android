package io.deus.wallet.modules.coin.coinmarkets

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.MarketField
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.WithTranslatableTitle
import io.horizontalsystems.marketkit.models.FullCoin
import java.math.BigDecimal

object CoinMarketsModule {
    class Factory(private val fullCoin: FullCoin) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = CoinMarketsService(fullCoin, App.currencyManager, App.marketKit)
            return CoinMarketsViewModel(service) as T
        }
    }

    @Immutable
    data class Menu(
        val sortDescending: Boolean,
        val marketFieldSelect: Select<MarketField>
    )

    sealed class VolumeMenuType : WithTranslatableTitle {
        class Coin(val name: String) : VolumeMenuType()
        class Currency(val name: String) : VolumeMenuType()

        override val title: TranslatableString
            get() = when (this) {
                is Coin -> TranslatableString.PlainString(name)
                is Currency -> TranslatableString.PlainString(name)
            }
    }
}

data class MarketTickerItem(
    val market: String,
    val marketImageUrl: String?,
    val baseCoinCode: String,
    val targetCoinCode: String,
    val volumeFiat: BigDecimal,
    val volumeToken: BigDecimal,
    val tradeUrl: String?,
    val verified: Boolean
)

enum class VerifiedType: WithTranslatableTitle  {
    Verified, All;

    override val title: TranslatableString
        get() = when(this) {
            Verified -> TranslatableString.ResString(R.string.CoinPage_MarketsVerifiedMenu_Verified)
            All -> TranslatableString.ResString(R.string.CoinPage_MarketsVerifiedMenu_All)
        }

    fun next() = values()[if (ordinal == values().size - 1) 0 else ordinal + 1]
}
