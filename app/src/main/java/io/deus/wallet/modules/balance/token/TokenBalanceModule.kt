package io.deus.wallet.modules.balance.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.balance.BalanceAdapterRepository
import io.deus.wallet.modules.balance.BalanceCache
import io.deus.wallet.modules.balance.BalanceViewItem
import io.deus.wallet.modules.balance.BalanceViewItemFactory
import io.deus.wallet.modules.balance.BalanceXRateRepository
import io.deus.wallet.modules.transactions.NftMetadataService
import io.deus.wallet.modules.transactions.TransactionRecordRepository
import io.deus.wallet.modules.transactions.TransactionSyncStateRepository
import io.deus.wallet.modules.transactions.TransactionViewItem
import io.deus.wallet.modules.transactions.TransactionViewItemFactory
import io.deus.wallet.modules.transactions.TransactionsRateRepository

class TokenBalanceModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val balanceService = TokenBalanceService(
                wallet,
                BalanceXRateRepository("wallet", App.currencyManager, App.marketKit),
                BalanceAdapterRepository(App.adapterManager, BalanceCache(App.appDatabase.enabledWalletsCacheDao())),
            )

            val tokenTransactionsService = TokenTransactionsService(
                wallet,
                TransactionRecordRepository(App.transactionAdapterManager),
                TransactionsRateRepository(App.currencyManager, App.marketKit),
                TransactionSyncStateRepository(App.transactionAdapterManager),
                App.contactsRepository,
                NftMetadataService(App.nftMetadataManager),
                App.spamManager
            )

            return TokenBalanceViewModel(
                wallet,
                balanceService,
                BalanceViewItemFactory(),
                tokenTransactionsService,
                TransactionViewItemFactory(App.evmLabelManager, App.contactsRepository, App.balanceHiddenManager),
                App.balanceHiddenManager,
                App.connectivityManager,
                App.accountManager,
            ) as T
        }
    }

    data class TokenBalanceUiState(
        val title: String,
        val balanceViewItem: BalanceViewItem?,
        val transactions: Map<String, List<TransactionViewItem>>?,
    )
}
