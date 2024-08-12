package io.deus.wallet.modules.multiswap.sendtransaction

import io.deus.wallet.modules.evmfee.GasPriceInfo
import io.horizontalsystems.ethereumkit.models.Address

sealed class SendTransactionSettings {
    data class Evm(val gasPriceInfo: GasPriceInfo?, val receiveAddress: Address) : SendTransactionSettings()
}
