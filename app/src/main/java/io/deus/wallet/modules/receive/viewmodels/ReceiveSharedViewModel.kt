package io.deus.wallet.modules.receive.viewmodels

import androidx.lifecycle.ViewModel
import io.deus.wallet.core.App
import io.deus.wallet.entities.Account
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.receive.ui.UsedAddressesParams
import io.horizontalsystems.marketkit.models.FullCoin

class ReceiveSharedViewModel : ViewModel() {

    var wallet: Wallet? = null
    var coinUid: String? = null
    var usedAddressesParams: UsedAddressesParams? = null

    val activeAccount: Account?
        get() = App.accountManager.activeAccount

    fun fullCoin(): FullCoin? {
        val coinUid = coinUid ?: return null
        return App.marketKit.fullCoins(listOf(coinUid)).firstOrNull()
    }

}