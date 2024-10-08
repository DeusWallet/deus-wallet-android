package io.deus.wallet.modules.send.zcash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.ISendZcashAdapter
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.amount.AmountValidator
import io.deus.wallet.modules.amount.SendAmountService
import io.deus.wallet.modules.xrate.XRateService

object SendZCashModule {

    class Factory(
        private val wallet: Wallet,
        private val predefinedAddress: String?,
    ) : ViewModelProvider.Factory {
        val adapter =
            (App.adapterManager.getAdapterForWallet(wallet) as? ISendZcashAdapter) ?: throw IllegalStateException("SendZcashAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val xRateService = XRateService(App.marketKit, App.currencyManager.baseCurrency)
            val amountService = SendAmountService(
                AmountValidator(),
                wallet.coin.code,
                adapter.availableBalance
            )
            val addressService = SendZCashAddressService(adapter, predefinedAddress)
            val memoService = SendZCashMemoService()

            return SendZCashViewModel(
                adapter,
                wallet,
                xRateService,
                amountService,
                addressService,
                memoService,
                App.contactsRepository,
                predefinedAddress == null
            ) as T
        }
    }
}
