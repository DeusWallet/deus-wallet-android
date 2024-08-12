package io.deus.wallet.entities.transactionrecords.evm

import io.deus.wallet.entities.TransactionValue

data class TransferEvent(
    val address: String?,
    val value: TransactionValue
)
