package io.deus.wallet.modules.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.AdapterState
import io.deus.wallet.core.App
import io.deus.wallet.core.BalanceData
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.address.AddressHandlerFactory
import io.deus.wallet.modules.balance.cex.BalanceCexRepositoryWrapper
import io.deus.wallet.modules.balance.cex.BalanceCexSorter
import io.deus.wallet.modules.balance.cex.BalanceCexViewModel
import io.horizontalsystems.marketkit.models.CoinPrice

object BalanceModule {
    class AccountsFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BalanceAccountsViewModel(App.accountManager) as T
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )
            return BalanceViewModel(
                BalanceService.getInstance("wallet"),
                BalanceViewItemFactory(),
                App.balanceViewTypeManager,
                TotalBalance(totalService, App.balanceHiddenManager),
                App.localStorage,
                App.wcManager,
                AddressHandlerFactory(App.appConfigProvider.udnApiKey),
            ) as T
        }
    }

    class FactoryCex : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )

            return BalanceCexViewModel(
                TotalBalance(totalService, App.balanceHiddenManager),
                App.localStorage,
                App.balanceViewTypeManager,
                BalanceViewItemFactory(),
                BalanceCexRepositoryWrapper(App.cexAssetManager, App.connectivityManager),
                BalanceXRateRepository("wallet", App.currencyManager, App.marketKit),
                BalanceCexSorter(),
                App.cexProviderManager,
            ) as T
        }
    }

    data class BalanceItem(
        val wallet: Wallet,
        val balanceData: BalanceData,
        val state: AdapterState,
        val sendAllowed: Boolean,
        val coinPrice: CoinPrice? = null
    ) {
        val fiatValue get() = coinPrice?.value?.let { balanceData.available.times(it) }
        val balanceFiatTotal get() = coinPrice?.value?.let { balanceData.total.times(it) }
    }
}