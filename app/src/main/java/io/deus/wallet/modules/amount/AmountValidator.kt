package io.deus.wallet.modules.amount

import io.deus.wallet.R
import io.deus.wallet.core.HSCaution
import io.deus.wallet.modules.send.SendErrorInsufficientBalance
import io.deus.wallet.modules.send.SendErrorMaximumSendAmount
import io.deus.wallet.modules.send.SendErrorMinimumSendAmount
import io.deus.wallet.ui.compose.TranslatableString
import java.math.BigDecimal

class AmountValidator {

    fun validate(
        coinAmount: BigDecimal?,
        coinCode: String,
        availableBalance: BigDecimal,
        minimumSendAmount: BigDecimal? = null,
        maximumSendAmount: BigDecimal? = null,
        leaveSomeBalanceForFee: Boolean = false
    ) = when {
        coinAmount == null -> null
        coinAmount == BigDecimal.ZERO -> null
        coinAmount > availableBalance -> {
            SendErrorInsufficientBalance(coinCode)
        }
        minimumSendAmount != null && coinAmount < minimumSendAmount -> {
            SendErrorMinimumSendAmount(minimumSendAmount)
        }
        maximumSendAmount != null && coinAmount > maximumSendAmount -> {
            SendErrorMaximumSendAmount(maximumSendAmount)
        }
        leaveSomeBalanceForFee && coinAmount == availableBalance -> {
            HSCaution(
                TranslatableString.ResString(R.string.EthereumTransaction_Warning_CoinNeededForFee, coinCode),
                HSCaution.Type.Warning
            )
        }
        else -> null
    }

}
