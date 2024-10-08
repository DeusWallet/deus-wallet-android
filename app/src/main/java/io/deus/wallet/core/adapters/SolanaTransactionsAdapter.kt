package io.deus.wallet.core.adapters

import io.deus.wallet.core.AdapterState
import io.deus.wallet.core.App
import io.deus.wallet.core.ITransactionsAdapter
import io.deus.wallet.core.managers.SolanaKitWrapper
import io.deus.wallet.entities.LastBlockInfo
import io.deus.wallet.entities.transactionrecords.TransactionRecord
import io.deus.wallet.modules.transactions.FilterTransactionType
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenType
import io.horizontalsystems.solanakit.SolanaKit
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.rx2.rxSingle

class SolanaTransactionsAdapter(
        solanaKitWrapper: SolanaKitWrapper,
        private val solanaTransactionConverter: SolanaTransactionConverter
) : ITransactionsAdapter {

    private val kit = solanaKitWrapper.solanaKit

    override val explorerTitle: String
        get() = "Solscan.io"

    override fun getTransactionUrl(transactionHash: String): String =
        "https://solscan.io/tx/$transactionHash"

    override val lastBlockInfo: LastBlockInfo?
        get() = kit.lastBlockHeight?.toInt()?.let { LastBlockInfo(it) }

    override val lastBlockUpdatedFlowable: Flowable<Unit>
        get() = kit.lastBlockHeightFlow.map {}.asFlowable()

    override val transactionsState: AdapterState
        get() = convertToAdapterState(kit.transactionsSyncState)

    override val transactionsStateUpdatedFlowable: Flowable<Unit>
        get() = kit.transactionsSyncStateFlow.map {}.asFlowable()

    override fun getTransactionsAsync(
        from: TransactionRecord?,
        token: Token?,
        limit: Int,
        transactionType: FilterTransactionType,
        address: String?,
    ) = when (address) {
        null -> getTransactionsAsync(from, token, limit, transactionType)
        else -> Single.just(listOf())
    }

    private fun getTransactionsAsync(
        from: TransactionRecord?,
        token: Token?,
        limit: Int,
        transactionType: FilterTransactionType,
    ): Single<List<TransactionRecord>> {
        val incoming = when (transactionType) {
            FilterTransactionType.All -> null
            FilterTransactionType.Incoming -> true
            FilterTransactionType.Outgoing -> false
            else -> return Single.just(listOf())
        }

        val transactions = rxSingle(Dispatchers.IO) {
            when {
                token == null -> kit.getAllTransactions(incoming, from?.transactionHash, limit)
                token.type is TokenType.Native -> kit.getSolTransactions(incoming, from?.transactionHash, limit)
                token.type is TokenType.Spl -> kit.getSplTransactions((token.type as TokenType.Spl).address, incoming, from?.transactionHash, limit)
                else -> listOf()
            }
        }

        return transactions.map { txList ->
            txList.map { solanaTransactionConverter.transactionRecord(it) }
        }
    }

    override fun getTransactionRecordsFlowable(
        token: Token?,
        transactionType: FilterTransactionType,
        address: String?,
    ): Flowable<List<TransactionRecord>> = when (address) {
        null -> getTransactionRecordsFlowable(token, transactionType)
        else -> Flowable.empty()
    }

    private fun getTransactionRecordsFlowable(token: Token?, transactionType: FilterTransactionType): Flowable<List<TransactionRecord>> {
        val incoming: Boolean? = when (transactionType) {
            FilterTransactionType.All -> null
            FilterTransactionType.Incoming -> true
            FilterTransactionType.Outgoing -> false
            else -> return Flowable.just(listOf())
        }

        val transactionsFlow =  when {
            token == null -> kit.allTransactionsFlow(incoming)
            token.type is TokenType.Native -> kit.solTransactionsFlow(incoming)
            token.type is TokenType.Spl -> kit.splTransactionsFlow((token.type as TokenType.Spl).address, incoming)
            else -> emptyFlow()
        }

        return transactionsFlow.map { txList ->
            txList.map { solanaTransactionConverter.transactionRecord(it) }
        }.asFlowable()
    }

    private fun convertToAdapterState(syncState: SolanaKit.SyncState): AdapterState =
            when (syncState) {
                is SolanaKit.SyncState.Synced -> AdapterState.Synced
                is SolanaKit.SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
                is SolanaKit.SyncState.Syncing -> AdapterState.Syncing()
            }

    companion object {
        const val decimal = 18

        fun clear(walletId: String) {
            SolanaKit.clear(App.instance, walletId)
        }
    }
}
