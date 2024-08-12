package io.deus.wallet.modules.send

import io.deus.wallet.entities.Address
import io.deus.wallet.modules.contacts.model.Contact
import io.horizontalsystems.hodler.LockTimeInterval
import io.horizontalsystems.marketkit.models.Coin
import java.math.BigDecimal

data class SendConfirmationData(
    val amount: BigDecimal,
    val fee: BigDecimal,
    val address: Address,
    val contact: Contact?,
    val coin: Coin,
    val feeCoin: Coin,
    val lockTimeInterval: LockTimeInterval? = null,
    val memo: String?,
    val rbfEnabled: Boolean? = null
)
