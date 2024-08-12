package io.deus.wallet.entities.transactionrecords.tron

import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronOutgoingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val to: String,
    val value: TransactionValue,
    val sentToSelf: Boolean
) : TronTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}
