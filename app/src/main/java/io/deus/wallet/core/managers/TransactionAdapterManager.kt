package io.deus.wallet.core.managers

import io.deus.wallet.core.IAdapter
import io.deus.wallet.core.IAdapterManager
import io.deus.wallet.core.ITransactionsAdapter
import io.deus.wallet.core.factories.AdapterFactory
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.BlockchainType
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.ConcurrentHashMap

class TransactionAdapterManager(
    private val adapterManager: IAdapterManager,
    private val adapterFactory: AdapterFactory
) {
    private val disposables = CompositeDisposable()

    private val _adaptersReadyFlow =
        MutableSharedFlow<Map<TransactionSource, ITransactionsAdapter>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val adaptersReadyFlow get() = _adaptersReadyFlow.asSharedFlow()

    val adaptersMap = ConcurrentHashMap<TransactionSource, ITransactionsAdapter>()

    init {
        adapterManager.adaptersReadyObservable
            .subscribeIO {
                initAdapters(it)
            }
            .let {
                disposables.add(it)
            }
    }

    fun getAdapter(source: TransactionSource): ITransactionsAdapter? = adaptersMap[source]

    private fun initAdapters(adaptersMap: Map<Wallet, IAdapter>) {
        val currentAdapters = this.adaptersMap.toMutableMap()
        this.adaptersMap.clear()

        for ((wallet, adapter) in adaptersMap) {
            val source = wallet.transactionSource
            if (this.adaptersMap.containsKey(source)) continue

            var txAdapter = currentAdapters.remove(source)
            if (txAdapter == null) {
                txAdapter = when (val blockchainType = source.blockchain.type) {
                    BlockchainType.Ethereum,
                    BlockchainType.BinanceSmartChain,
                    BlockchainType.Polygon,
                    BlockchainType.Avalanche,
                    BlockchainType.Optimism,
                    BlockchainType.Gnosis,
                    BlockchainType.Fantom,
                    BlockchainType.ArbitrumOne -> {
                        adapterFactory.evmTransactionsAdapter(wallet.transactionSource, blockchainType)
                    }
                    BlockchainType.Solana -> {
                        adapterFactory.solanaTransactionsAdapter(wallet.transactionSource)
                    }
                    BlockchainType.Tron -> {
                        adapterFactory.tronTransactionsAdapter(wallet.transactionSource)
                    }
                    else -> adapter as? ITransactionsAdapter
                }
            }

            txAdapter?.let {
                this.adaptersMap[source] = it
            }
        }

        currentAdapters.forEach {
            adapterFactory.unlinkAdapter(it.key)
        }

        _adaptersReadyFlow.tryEmit(this.adaptersMap)
    }
}
