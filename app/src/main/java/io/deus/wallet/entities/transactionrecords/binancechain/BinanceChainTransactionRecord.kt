package io.deus.wallet.entities.transactionrecords.binancechain

import io.deus.wallet.core.adapters.BinanceAdapter
import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.entities.transactionrecords.TransactionRecord
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

abstract class BinanceChainTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    source: TransactionSource
) : TransactionRecord(
    uid = transaction.hash,
    transactionHash = transaction.hash,
    transactionIndex = 0,
    blockHeight = transaction.blockNumber,
    confirmationsThreshold = BinanceAdapter.confirmationsThreshold,
    timestamp = transaction.date.time / 1000,
    failed = false,
    source = source
) {

    val fee = TransactionValue.CoinValue(feeToken, BinanceAdapter.transferFee)
    val memo = transaction.memo

}
