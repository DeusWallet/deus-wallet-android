package io.deus.wallet.entities.transactionrecords.tron

import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronApproveTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val spender: String,
    val value: TransactionValue
) : TronTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}
