package io.deus.wallet.entities.transactionrecords.tron

import io.deus.wallet.core.managers.SpamManager
import io.deus.wallet.entities.TransactionValue
import io.deus.wallet.entities.transactionrecords.evm.EvmTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.TransferEvent
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronExternalContractCallTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    spamManager: SpamManager,
    val incomingEvents: List<TransferEvent>,
    val outgoingEvents: List<TransferEvent>
) : TronTransactionRecord(
    transaction = transaction,
    baseToken = baseToken,
    source = source,
    foreignTransaction = true,
    spam = spamManager.isSpam(incomingEvents, outgoingEvents)
) {

    override val mainValue: TransactionValue?
        get() {
            val (incomingValues, outgoingValues) = EvmTransactionRecord.combined(incomingEvents, outgoingEvents)

            return when {
                (incomingValues.isEmpty() && outgoingValues.size == 1) -> outgoingValues.first()
                (incomingValues.size == 1 && outgoingValues.isEmpty()) -> incomingValues.first()
                else -> null
            }
        }

}
