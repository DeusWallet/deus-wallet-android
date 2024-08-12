package io.deus.wallet.modules.swap.approve

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.adapters.Eip20Adapter
import io.deus.wallet.core.ethereum.EvmCoinService
import io.deus.wallet.modules.swap.SwapMainModule
import io.horizontalsystems.ethereumkit.models.Address

object SwapApproveModule {

    class Factory(private val approveData: SwapMainModule.ApproveData) :
        ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SwapApproveViewModel::class.java -> {
                    val wallet =
                        checkNotNull(App.walletManager.activeWallets.firstOrNull { it.token == approveData.token })
                    val erc20Adapter =
                        App.adapterManager.getAdapterForWallet(wallet) as Eip20Adapter
                    val approveAmountBigInteger =
                        approveData.amount.movePointRight(approveData.token.decimals).toBigInteger()
                    val allowanceAmountBigInteger =
                        approveData.allowance.movePointRight(approveData.token.decimals)
                            .toBigInteger()
                    val swapApproveService = SwapApproveService(
                        erc20Adapter.eip20Kit,
                        approveAmountBigInteger,
                        Address(approveData.spenderAddress),
                        allowanceAmountBigInteger
                    )
                    val coinService by lazy {
                        EvmCoinService(approveData.token, App.currencyManager, App.marketKit)
                    }
                    SwapApproveViewModel(
                        approveData.blockchainType,
                        swapApproveService,
                        coinService
                    ) as T
                }

                else -> throw IllegalArgumentException()
            }
        }
    }
}
