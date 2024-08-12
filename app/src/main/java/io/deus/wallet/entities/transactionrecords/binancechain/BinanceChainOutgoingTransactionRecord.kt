package io.deus.wallet.entities.transactionrecords.binancechain

import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

class BinanceChainOutgoingTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    token: Token,
    val sentToSelf: Boolean,
    source: TransactionSource
) : BinanceChainTransactionRecord(transaction, feeToken, source) {
    val value = TransactionValue.CoinValue(token, transaction.amount.toBigDecimal().negate())
    val to = transaction.to

    override val mainValue = value

}
