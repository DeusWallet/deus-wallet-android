package io.deus.wallet.modules.market.favorites

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.modules.market.MarketViewItem
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.WithTranslatableTitle
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

object MarketFavoritesModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = MarketFavoritesRepository(App.marketKit, App.marketFavoritesManager)
            val menuService = MarketFavoritesMenuService(App.localStorage, App.marketWidgetManager)
            val service = MarketFavoritesService(repository, menuService, App.currencyManager, App.backgroundManager)
            return MarketFavoritesViewModel(service) as T
        }
    }

    @Immutable
    data class ViewItem(
        val sortingDescending: Boolean,
        val periodSelect: Select<Period>,
        val marketItems: List<MarketViewItem>
    )

    @Parcelize
    enum class Period(val titleResId: Int) : WithTranslatableTitle, Parcelable {
        OneDay(R.string.CoinPage_TimeDuration_Day),
        SevenDay(R.string.CoinPage_TimeDuration_Week),
        ThirtyDay(R.string.CoinPage_TimeDuration_Month);

        @IgnoredOnParcel
        override val title = TranslatableString.ResString(titleResId)
    }
}
