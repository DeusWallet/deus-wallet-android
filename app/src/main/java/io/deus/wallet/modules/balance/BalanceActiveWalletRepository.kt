package io.deus.wallet.modules.balance

import io.deus.wallet.core.IWalletManager
import io.deus.wallet.core.managers.EvmSyncSourceManager
import io.deus.wallet.entities.Wallet
import io.reactivex.Observable

class BalanceActiveWalletRepository(
    private val walletManager: IWalletManager,
    evmSyncSourceManager: EvmSyncSourceManager
) {

    val itemsObservable: Observable<List<Wallet>> =
        Observable
            .merge(
                Observable.just(Unit),
                walletManager.activeWalletsUpdatedObservable,
                evmSyncSourceManager.syncSourceObservable
            )
            .map {
                walletManager.activeWallets
            }

    fun disable(wallet: Wallet) {
        walletManager.delete(listOf(wallet))
    }

    fun enable(wallet: Wallet) {
        walletManager.save(listOf(wallet))
    }

}
