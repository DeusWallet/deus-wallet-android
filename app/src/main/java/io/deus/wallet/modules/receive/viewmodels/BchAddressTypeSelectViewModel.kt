package io.deus.wallet.modules.receive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.IWalletManager
import io.deus.wallet.core.bitcoinCashCoinType
import io.deus.wallet.modules.receive.ui.AddressFormatItem
import io.horizontalsystems.marketkit.models.TokenType

class BchAddressTypeSelectViewModel(coinUid: String, walletManager: IWalletManager) : ViewModel() {
    val items = walletManager.activeWallets
        .filter {
            it.coin.uid == coinUid
        }
        .mapNotNull { wallet ->
            val addressType =
                (wallet.token.type as? TokenType.AddressTyped)?.type ?: return@mapNotNull null
            val bitcoinCashCoinType = addressType.bitcoinCashCoinType

            AddressFormatItem(
                title = bitcoinCashCoinType.title,
                subtitle = bitcoinCashCoinType.value.uppercase(),
                wallet = wallet
            )
        }

    class Factory(private val coinUid: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BchAddressTypeSelectViewModel(coinUid, App.walletManager) as T
        }
    }
}

