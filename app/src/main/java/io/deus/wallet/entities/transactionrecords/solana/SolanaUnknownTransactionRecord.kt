package io.deus.wallet.entities.transactionrecords.solana

import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction

class SolanaUnknownTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val incomingTransfers: List<Transfer>,
        val outgoingTransfers: List<Transfer>
): SolanaTransactionRecord(transaction, baseToken, source)
