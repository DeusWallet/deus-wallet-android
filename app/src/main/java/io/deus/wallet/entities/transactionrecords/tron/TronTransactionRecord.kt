package io.deus.wallet.entities.transactionrecords.tron

import io.deus.wallet.core.adapters.BaseTronAdapter
import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.entities.transactionrecords.TransactionRecord
import io.deus.wallet.modules.transactions.TransactionSource
import io.deus.wallet.modules.transactions.TransactionStatus
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

open class TronTransactionRecord(
    val transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val foreignTransaction: Boolean = false,
    spam: Boolean = false
) :
    TransactionRecord(
        uid = transaction.hashString,
        transactionHash = transaction.hashString,
        transactionIndex = 0,
        blockHeight = transaction.blockNumber?.toInt(),
        confirmationsThreshold = BaseTronAdapter.confirmationsThreshold,
        timestamp = transaction.timestamp / 1000,
        failed = transaction.isFailed,
        spam = spam,
        source = source
    ) {

    val fee: TransactionValue?

    init {
        val feeAmount: Long? = transaction.fee
        fee = if (feeAmount != null) {
            val feeDecimal = feeAmount.toBigDecimal()
                .movePointLeft(baseToken.decimals).stripTrailingZeros()

            TransactionValue.CoinValue(baseToken, feeDecimal)
        } else {
            null
        }
    }

    override fun status(lastBlockHeight: Int?): TransactionStatus {
        when {
            failed -> {
                return TransactionStatus.Failed
            }

            transaction.confirmed -> {
                return TransactionStatus.Completed
            }

            blockHeight != null && lastBlockHeight != null -> {
                val threshold = confirmationsThreshold ?: 1
                val confirmations = lastBlockHeight - blockHeight.toInt() + 1

                return if (confirmations >= threshold) {
                    TransactionStatus.Completed
                } else {
                    TransactionStatus.Processing(confirmations.toFloat() / threshold.toFloat())
                }
            }

            else -> return TransactionStatus.Pending
        }
    }

}
