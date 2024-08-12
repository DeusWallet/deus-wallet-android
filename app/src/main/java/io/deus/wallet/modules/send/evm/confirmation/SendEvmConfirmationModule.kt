package io.deus.wallet.modules.send.evm.confirmation

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.deus.wallet.core.App
import io.deus.wallet.core.ethereum.CautionViewItemFactory
import io.deus.wallet.core.ethereum.EvmCoinServiceFactory
import io.deus.wallet.core.managers.EvmKitWrapper
import io.deus.wallet.modules.evmfee.EvmCommonGasDataService
import io.deus.wallet.modules.evmfee.EvmFeeCellViewModel
import io.deus.wallet.modules.evmfee.EvmFeeService
import io.deus.wallet.modules.evmfee.IEvmGasPriceService
import io.deus.wallet.modules.evmfee.eip1559.Eip1559GasPriceService
import io.deus.wallet.modules.evmfee.legacy.LegacyGasPriceService
import io.deus.wallet.modules.send.evm.SendEvmData
import io.deus.wallet.modules.send.evm.SendEvmModule
import io.deus.wallet.modules.send.evm.settings.SendEvmNonceService
import io.deus.wallet.modules.send.evm.settings.SendEvmNonceViewModel
import io.deus.wallet.modules.send.evm.settings.SendEvmSettingsService
import io.deus.wallet.modules.sendevmtransaction.SendEvmTransactionService
import io.deus.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.Chain
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize

object SendEvmConfirmationModule {

    class Factory(
        private val evmKitWrapper: EvmKitWrapper,
        private val sendEvmData: SendEvmData
    ) : ViewModelProvider.Factory {

        private val blockchainType = when (evmKitWrapper.evmKit.chain) {
            Chain.BinanceSmartChain -> BlockchainType.BinanceSmartChain
            Chain.Polygon -> BlockchainType.Polygon
            Chain.Avalanche -> BlockchainType.Avalanche
            Chain.Optimism -> BlockchainType.Optimism
            Chain.ArbitrumOne -> BlockchainType.ArbitrumOne
            Chain.Gnosis -> BlockchainType.Gnosis
            Chain.Fantom -> BlockchainType.Fantom
            else -> BlockchainType.Ethereum
        }

        private val feeToken by lazy {
            App.evmBlockchainManager.getBaseToken(blockchainType)!!
        }
        private val gasPriceService: IEvmGasPriceService by lazy {
            val evmKit = evmKitWrapper.evmKit
            if (evmKit.chain.isEIP1559Supported) {
                val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                Eip1559GasPriceService(gasPriceProvider, evmKit)
            } else {
                val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                LegacyGasPriceService(gasPriceProvider)
            }
        }
        private val feeService by lazy {
            val gasDataService = EvmCommonGasDataService.instance(
                evmKitWrapper.evmKit,
                evmKitWrapper.blockchainType
            )
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, sendEvmData.transactionData)
        }
        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                feeToken,
                App.marketKit,
                App.currencyManager,
                App.coinManager
            )
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }
        private val nonceService by lazy { SendEvmNonceService(evmKitWrapper.evmKit) }
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }
        private val sendService by lazy {
            SendEvmTransactionService(sendEvmData, evmKitWrapper, settingsService, App.evmLabelManager)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        sendService,
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = blockchainType,
                        contactsRepo = App.contactsRepository
                    ) as T
                }
                EvmFeeCellViewModel::class.java -> {
                    EvmFeeCellViewModel(feeService, gasPriceService, coinServiceFactory.baseCoinService) as T
                }
                SendEvmNonceViewModel::class.java -> {
                    SendEvmNonceViewModel(nonceService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }


    @Parcelize
    data class Input(
        val transactionDataParcelable: SendEvmModule.TransactionDataParcelable,
        val additionalInfo: SendEvmData.AdditionalInfo?,
        val sendNavId: Int,
        val sendEntryPointDestId: Int
    ) : Parcelable {
        val transactionData: TransactionData
            get() = TransactionData(
                Address(transactionDataParcelable.toAddress),
                transactionDataParcelable.value,
                transactionDataParcelable.input
            )

        constructor(sendData: SendEvmData, sendNavId: Int, sendEntryPointDestId: Int = 0) : this(
            SendEvmModule.TransactionDataParcelable(sendData.transactionData),
            sendData.additionalInfo,
            sendNavId,
            sendEntryPointDestId
        )
    }

}
