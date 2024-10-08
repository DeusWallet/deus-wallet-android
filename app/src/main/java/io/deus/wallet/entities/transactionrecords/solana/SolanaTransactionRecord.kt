package io.deus.wallet.entities.transactionrecords.solana

import io.deus.wallet.core.adapters.BaseSolanaAdapter
import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.entities.transactionrecords.TransactionRecord
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction

open class SolanaTransactionRecord(transaction: Transaction, baseToken: Token, source: TransactionSource, spam: Boolean = false) :
        TransactionRecord(
                uid = transaction.hash,
                transactionHash = transaction.hash,
                transactionIndex = 0,
                blockHeight = if (transaction.pending) null else 0,
                confirmationsThreshold = BaseSolanaAdapter.confirmationsThreshold,
                timestamp = transaction.timestamp,
                failed = transaction.error != null,
                spam = spam,
                source = source
        ) {

    data class Transfer(val address: String?, val value: TransactionValue)

    val fee: TransactionValue?

    init {
        fee = transaction.fee?.let { TransactionValue.CoinValue(baseToken, it) }
    }

}
