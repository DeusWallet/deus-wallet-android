package io.deus.wallet.entities.transactionrecords.tron

import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronIncomingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val from: String,
    val value: TransactionValue,
    spam: Boolean
) : TronTransactionRecord(transaction, baseToken, source, true, spam) {

    override val mainValue = value

}
