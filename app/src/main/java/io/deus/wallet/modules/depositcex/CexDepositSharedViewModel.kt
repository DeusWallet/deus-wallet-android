package io.deus.wallet.modules.depositcex

import androidx.lifecycle.ViewModel
import io.deus.wallet.core.providers.CexAsset
import io.deus.wallet.core.providers.CexDepositNetwork
import io.deus.wallet.modules.receive.ui.UsedAddressesParams

class CexDepositSharedViewModel : ViewModel() {

    var network: CexDepositNetwork? = null
    var cexAsset: CexAsset? = null
    var usedAddressesParams: UsedAddressesParams? = null

}