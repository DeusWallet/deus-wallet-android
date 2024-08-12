package io.deus.wallet.modules.swap.coinselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.modules.swap.SwapMainModule

object SelectSwapCoinModule {

    class Factory(private val dex: SwapMainModule.Dex) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val coinProvider by lazy {
                SwapCoinProvider(
                    dex,
                    App.walletManager,
                    App.adapterManager,
                    App.currencyManager,
                    App.marketKit
                )
            }
            return SelectSwapCoinViewModel(coinProvider) as T
        }
    }

}
