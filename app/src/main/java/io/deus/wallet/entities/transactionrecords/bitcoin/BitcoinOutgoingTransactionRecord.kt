package io.deus.wallet.entities.transactionrecords.bitcoin

import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.modules.transactions.TransactionLockInfo
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import java.math.BigDecimal

class BitcoinOutgoingTransactionRecord(
    token: Token,
    uid: String,
    transactionHash: String,
    transactionIndex: Int,
    blockHeight: Int?,
    confirmationsThreshold: Int?,
    timestamp: Long,
    fee: BigDecimal?,
    failed: Boolean,
    lockInfo: TransactionLockInfo?,
    conflictingHash: String?,
    showRawTransaction: Boolean,
    amount: BigDecimal,
    val to: String?,
    val sentToSelf: Boolean,
    memo: String?,
    source: TransactionSource,
    val replaceable: Boolean
) : BitcoinTransactionRecord(
    uid = uid,
    transactionHash = transactionHash,
    transactionIndex = transactionIndex,
    blockHeight = blockHeight,
    confirmationsThreshold = confirmationsThreshold,
    timestamp = timestamp,
    fee = fee?.let { TransactionValue.CoinValue(token, it) },
    failed = failed,
    lockInfo = lockInfo,
    conflictingHash = conflictingHash,
    showRawTransaction = showRawTransaction,
    memo = memo,
    source = source
) {
    val value: TransactionValue = TransactionValue.CoinValue(token, amount)

    override val mainValue = value

}
