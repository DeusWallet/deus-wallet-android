package io.deus.wallet.modules.transactionInfo.resendbitcoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.adapters.BitcoinBaseAdapter
import io.deus.wallet.core.factories.FeeRateProviderFactory
import io.deus.wallet.entities.transactionrecords.bitcoin.BitcoinOutgoingTransactionRecord
import io.deus.wallet.modules.transactionInfo.options.TransactionInfoOptionsModule
import io.deus.wallet.modules.transactions.TransactionSource
import io.deus.wallet.modules.xrate.XRateService

object ResendBitcoinModule {

    class Factory(
        private val optionType: TransactionInfoOptionsModule.Type,
        private val transactionRecord: BitcoinOutgoingTransactionRecord,
        private val source: TransactionSource
    ) : ViewModelProvider.Factory {

        private val adapter by lazy {
            App.transactionAdapterManager.getAdapter(source) as BitcoinBaseAdapter
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val replacementInfo = when (optionType) {
                TransactionInfoOptionsModule.Type.SpeedUp -> adapter.speedUpTransactionInfo(transactionRecord.transactionHash)
                TransactionInfoOptionsModule.Type.Cancel -> adapter.cancelTransactionInfo(transactionRecord.transactionHash)
            }

            return ResendBitcoinViewModel(
                type = optionType,
                transactionRecord = transactionRecord,
                replacementInfo = replacementInfo,
                adapter = adapter,
                xRateService = XRateService(App.marketKit, App.currencyManager.baseCurrency),
                feeRateProvider =  FeeRateProviderFactory.provider(adapter.wallet.token.blockchainType)!!,
                contactsRepo = App.contactsRepository
            ) as T
        }
    }

}
