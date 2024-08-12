package io.deus.wallet.modules.transactionInfo

import io.deus.wallet.R
import io.deus.wallet.core.ITransactionsAdapter
import io.deus.wallet.core.adapters.TonTransactionRecord
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.providers.AppConfigProvider
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.CurrencyValue
import io.deus.wallet.entities.nft.NftAssetBriefMetadata
import io.deus.wallet.entities.nft.NftUid
import io.deus.wallet.entities.transactionrecords.TransactionRecord
import io.deus.wallet.entities.transactionrecords.binancechain.BinanceChainIncomingTransactionRecord
import io.deus.wallet.entities.transactionrecords.binancechain.BinanceChainOutgoingTransactionRecord
import io.deus.wallet.entities.transactionrecords.bitcoin.BitcoinIncomingTransactionRecord
import io.deus.wallet.entities.transactionrecords.bitcoin.BitcoinOutgoingTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.ApproveTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.ContractCallTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.EvmIncomingTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.EvmOutgoingTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.EvmTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.ExternalContractCallTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.SwapTransactionRecord
import io.deus.wallet.entities.transactionrecords.evm.UnknownSwapTransactionRecord
import io.deus.wallet.entities.transactionrecords.nftUids
import io.deus.wallet.entities.transactionrecords.solana.SolanaIncomingTransactionRecord
import io.deus.wallet.entities.transactionrecords.solana.SolanaOutgoingTransactionRecord
import io.deus.wallet.entities.transactionrecords.solana.SolanaUnknownTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronApproveTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronContractCallTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronExternalContractCallTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronIncomingTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronOutgoingTransactionRecord
import io.deus.wallet.entities.transactionrecords.tron.TronTransactionRecord
import io.deus.wallet.modules.transactions.FilterTransactionType
import io.deus.wallet.modules.transactions.NftMetadataService
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.core.ISystemInfoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class TransactionInfoService(
    val transactionRecord: TransactionRecord,
    private val adapter: ITransactionsAdapter,
    private val marketKit: MarketKitWrapper,
    private val currencyManager: CurrencyManager,
    private val nftMetadataService: NftMetadataService,
    private val systemInfoManager: ISystemInfoManager,
    private val appConfigProvider: AppConfigProvider,
    balanceHidden: Boolean,
) {

    val transactionHash: String get() = transactionRecord.transactionHash
    val source: TransactionSource get() = transactionRecord.source

    private val _transactionInfoItemFlow = MutableStateFlow<TransactionInfoItem?>(null)
    val transactionInfoItemFlow = _transactionInfoItemFlow.filterNotNull()

    var transactionInfoItem = TransactionInfoItem(
        transactionRecord,
        adapter.lastBlockInfo,
        TransactionInfoModule.ExplorerData(adapter.explorerTitle, adapter.getTransactionUrl(transactionRecord.transactionHash)),
        mapOf(),
        mapOf(),
        balanceHidden
    )
        private set(value) {
            field = value
            _transactionInfoItemFlow.update { value }
        }

    val appVersion: String
        get() {
            var appVersion = systemInfoManager.appVersion
            if (Translator.getString(R.string.is_release) == "false") {
                appVersion += " (${appConfigProvider.appBuild})"
            }

            return appVersion
        }

    private val coinUidsForRates: List<String>
        get() {
            val coinUids = mutableListOf<String?>()

            val txCoinTypes = when (val tx = transactionRecord) {
                is TonTransactionRecord -> listOf(tx.mainValue.coinUid, tx.fee?.coinUid)
                is EvmIncomingTransactionRecord -> listOf(tx.value.coinUid)
                is EvmOutgoingTransactionRecord -> listOf(tx.fee?.coinUid, tx.value.coinUid)
                is SwapTransactionRecord -> listOf(tx.fee, tx.valueIn, tx.valueOut).map { it?.coinUid }
                is UnknownSwapTransactionRecord -> listOf(tx.fee, tx.valueIn, tx.valueOut).map { it?.coinUid }
                is ApproveTransactionRecord -> listOf(tx.fee?.coinUid, tx.value.coinUid)
                is ContractCallTransactionRecord -> {
                    val tempCoinUidList = mutableListOf<String>()
                    tempCoinUidList.addAll(tx.incomingEvents.map { it.value.coinUid })
                    tempCoinUidList.addAll(tx.outgoingEvents.map { it.value.coinUid })
                    tempCoinUidList
                }
                is ExternalContractCallTransactionRecord -> {
                    val tempCoinUidList = mutableListOf<String>()
                    tempCoinUidList.addAll(tx.incomingEvents.map { it.value.coinUid })
                    tempCoinUidList.addAll(tx.outgoingEvents.map { it.value.coinUid })
                    tempCoinUidList
                }
                is BitcoinIncomingTransactionRecord -> listOf(tx.value.coinUid)
                is BitcoinOutgoingTransactionRecord -> listOf(tx.fee, tx.value).map { it?.coinUid }
                is BinanceChainIncomingTransactionRecord -> listOf(tx.value.coinUid)
                is BinanceChainOutgoingTransactionRecord -> listOf(tx.fee, tx.value).map { it.coinUid }
                is SolanaIncomingTransactionRecord -> listOf(tx.value.coinUid)
                is SolanaOutgoingTransactionRecord -> listOf(tx.fee?.coinUid, tx.value.coinUid)
                is SolanaUnknownTransactionRecord -> {
                    val tempCoinUidList = mutableListOf<String>()
                    tempCoinUidList.addAll(tx.incomingTransfers.map { it.value.coinUid })
                    tempCoinUidList.addAll(tx.outgoingTransfers.map { it.value.coinUid })
                    tempCoinUidList
                }
                is TronOutgoingTransactionRecord -> {
                    listOf(tx.value.coinUid, tx.fee?.coinUid)
                }
                is TronIncomingTransactionRecord -> {
                    listOf(tx.value.coinUid)
                }
                is TronApproveTransactionRecord -> {
                    listOf(tx.value.coinUid, tx.fee?.coinUid)
                }
                is TronContractCallTransactionRecord -> {
                    val tempCoinUidList = mutableListOf<String>()
                    tempCoinUidList.addAll(tx.incomingEvents.map { it.value.coinUid })
                    tempCoinUidList.addAll(tx.outgoingEvents.map { it.value.coinUid })
                    tempCoinUidList
                }
                is TronExternalContractCallTransactionRecord -> {
                    val tempCoinUidList = mutableListOf<String>()
                    tempCoinUidList.addAll(tx.incomingEvents.map { it.value.coinUid })
                    tempCoinUidList.addAll(tx.outgoingEvents.map { it.value.coinUid })
                    tempCoinUidList
                }
                else -> emptyList()
            }

            (transactionRecord as? EvmTransactionRecord)?.let { transactionRecord ->
                if (!transactionRecord.foreignTransaction) {
                    coinUids.add(transactionRecord.fee?.coinUid)
                }
            }

            (transactionRecord as? TronTransactionRecord)?.let { transactionRecord ->
                if (!transactionRecord.foreignTransaction) {
                    coinUids.add(transactionRecord.fee?.coinUid)
                }
            }

            coinUids.addAll(txCoinTypes)

            return coinUids.filterNotNull().filter { it.isNotBlank() }.distinct()
        }

    suspend fun start() = withContext(Dispatchers.IO) {
        _transactionInfoItemFlow.update { transactionInfoItem }

        launch {
            adapter.getTransactionRecordsFlowable(null, FilterTransactionType.All, null).asFlow()
                .collect { transactionRecords ->
                    val record = transactionRecords.find { it == transactionRecord }

                    if (record != null) {
                        handleRecordUpdate(record)
                    }
                }
        }

        launch {
            adapter.lastBlockUpdatedFlowable.asFlow()
                .collect {
                    handleLastBlockUpdate()
                }
        }

        launch {
            nftMetadataService.assetsBriefMetadataFlow.collect {
                handleNftMetadata(it)
            }
        }

        fetchRates()
        fetchNftMetadata()
    }

    private suspend fun fetchNftMetadata() {
        val nftUids = transactionRecord.nftUids
        val assetsBriefMetadata = nftMetadataService.assetsBriefMetadata(nftUids)

        handleNftMetadata(assetsBriefMetadata)

        if (nftUids.subtract(assetsBriefMetadata.keys).isNotEmpty()) {
            nftMetadataService.fetch(nftUids)
        }
    }

    private suspend fun fetchRates() = withContext(Dispatchers.IO) {
        val coinUids = coinUidsForRates
        val timestamp = transactionRecord.timestamp

        val rates = coinUids.mapNotNull { coinUid ->
            try {
                val rate = marketKit
                    .coinHistoricalPriceSingle(coinUid, currencyManager.baseCurrency.code, timestamp)
                    .await()
                if (rate != BigDecimal.ZERO) {
                    Pair(coinUid, CurrencyValue(currencyManager.baseCurrency, rate))
                } else {
                    null
                }
            } catch (error: Exception) {
                null
            }
        }.toMap()

        handleRates(rates)
    }

    @Synchronized
    private fun handleLastBlockUpdate() {
        transactionInfoItem = transactionInfoItem.copy(lastBlockInfo = adapter.lastBlockInfo)
    }

    @Synchronized
    private fun handleRecordUpdate(transactionRecord: TransactionRecord) {
        transactionInfoItem = transactionInfoItem.copy(record = transactionRecord)
    }

    @Synchronized
    private fun handleRates(rates: Map<String, CurrencyValue>) {
        transactionInfoItem = transactionInfoItem.copy(rates = rates)
    }

    @Synchronized
    private fun handleNftMetadata(nftMetadata: Map<NftUid, NftAssetBriefMetadata>) {
        transactionInfoItem = transactionInfoItem.copy(nftMetadata = nftMetadata)
    }

    fun getRawTransaction(): String? {
        return adapter.getRawTransaction(transactionRecord.transactionHash)
    }

}
