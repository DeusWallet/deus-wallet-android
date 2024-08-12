package io.deus.wallet.modules.balance.token

import androidx.lifecycle.viewModelScope
import io.deus.wallet.core.IAccountManager
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.badge
import io.deus.wallet.core.managers.BalanceHiddenManager
import io.deus.wallet.core.managers.ConnectivityManager
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.balance.BackupRequiredError
import io.deus.wallet.modules.balance.BalanceModule
import io.deus.wallet.modules.balance.BalanceViewItem
import io.deus.wallet.modules.balance.BalanceViewItemFactory
import io.deus.wallet.modules.balance.BalanceViewModel
import io.deus.wallet.modules.balance.BalanceViewType
import io.deus.wallet.modules.balance.token.TokenBalanceModule.TokenBalanceUiState
import io.deus.wallet.modules.transactions.TransactionItem
import io.deus.wallet.modules.transactions.TransactionViewItem
import io.deus.wallet.modules.transactions.TransactionViewItemFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TokenBalanceViewModel(
    private val wallet: Wallet,
    private val balanceService: TokenBalanceService,
    private val balanceViewItemFactory: BalanceViewItemFactory,
    private val transactionsService: TokenTransactionsService,
    private val transactionViewItem2Factory: TransactionViewItemFactory,
    private val balanceHiddenManager: BalanceHiddenManager,
    private val connectivityManager: ConnectivityManager,
    private val accountManager: IAccountManager
) : ViewModelUiState<TokenBalanceUiState>() {

    private val title = wallet.token.coin.code + wallet.token.badge?.let { " ($it)" }.orEmpty()
    private val disposables = CompositeDisposable()

    private var balanceViewItem: BalanceViewItem? = null
    private var transactions: Map<String, List<TransactionViewItem>>? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            balanceService.balanceItemFlow.collect { balanceItem ->
                balanceItem?.let {
                    updateBalanceViewItem(it)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            balanceHiddenManager.balanceHiddenFlow.collect {
                balanceService.balanceItem?.let {
                    updateBalanceViewItem(it)
                    transactionViewItem2Factory.updateCache()
                    transactionsService.refreshList()
                }
            }
        }

        transactionsService.itemsObservable
            .subscribeIO {
                updateTransactions(it)
            }
            .let {
                disposables.add(it)
            }

        viewModelScope.launch(Dispatchers.IO) {
            balanceService.start()
            delay(300)
            transactionsService.start()
        }
    }

    override fun createState() = TokenBalanceUiState(
        title = title,
        balanceViewItem = balanceViewItem,
        transactions = transactions,
    )

    private fun updateTransactions(items: List<TransactionItem>) {
        transactions = items
            .map { transactionViewItem2Factory.convertToViewItemCached(it) }
            .groupBy { it.formattedDate }

        emitState()
    }

    private fun updateBalanceViewItem(balanceItem: BalanceModule.BalanceItem) {
        val balanceViewItem = balanceViewItemFactory.viewItem(
            balanceItem,
            balanceService.baseCurrency,
            balanceHiddenManager.balanceHidden,
            wallet.account.isWatchAccount,
            BalanceViewType.CoinThenFiat
        )

        this.balanceViewItem = balanceViewItem.copy(
            primaryValue = balanceViewItem.primaryValue.copy(value = balanceViewItem.primaryValue.value + " " + balanceViewItem.coinCode)
        )

        emitState()
    }

    @Throws(BackupRequiredError::class, IllegalStateException::class)
    fun getWalletForReceive(): Wallet {
        val account = accountManager.activeAccount ?: throw IllegalStateException("Active account is not set")
        when {
            account.hasAnyBackup -> return wallet
            else -> throw BackupRequiredError(account, wallet.coin.name)
        }
    }

    fun onBottomReached() {
        transactionsService.loadNext()
    }

    fun willShow(viewItem: TransactionViewItem) {
        transactionsService.fetchRateIfNeeded(viewItem.uid)
    }

    fun getTransactionItem(viewItem: TransactionViewItem) = transactionsService.getTransactionItem(viewItem.uid)

    fun toggleBalanceVisibility() {
        balanceHiddenManager.toggleBalanceHidden()
    }

    fun getSyncErrorDetails(viewItem: BalanceViewItem): BalanceViewModel.SyncError = when {
        connectivityManager.isConnected -> BalanceViewModel.SyncError.Dialog(viewItem.wallet, viewItem.errorMessage)
        else -> BalanceViewModel.SyncError.NetworkNotAvailable()
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
        balanceService.clear()
    }

}
