package io.deus.wallet.modules.send

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.BaseFragment
import io.deus.wallet.core.requireInput
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.amount.AmountInputModeModule
import io.deus.wallet.modules.amount.AmountInputModeViewModel
import io.deus.wallet.modules.send.binance.SendBinanceModule
import io.deus.wallet.modules.send.binance.SendBinanceScreen
import io.deus.wallet.modules.send.binance.SendBinanceViewModel
import io.deus.wallet.modules.send.bitcoin.SendBitcoinModule
import io.deus.wallet.modules.send.bitcoin.SendBitcoinNavHost
import io.deus.wallet.modules.send.bitcoin.SendBitcoinViewModel
import io.deus.wallet.modules.send.evm.SendEvmModule
import io.deus.wallet.modules.send.evm.SendEvmScreen
import io.deus.wallet.modules.send.evm.SendEvmViewModel
import io.deus.wallet.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel
import io.deus.wallet.modules.send.solana.SendSolanaModule
import io.deus.wallet.modules.send.solana.SendSolanaScreen
import io.deus.wallet.modules.send.solana.SendSolanaViewModel
import io.deus.wallet.modules.send.ton.SendTonModule
import io.deus.wallet.modules.send.ton.SendTonScreen
import io.deus.wallet.modules.send.ton.SendTonViewModel
import io.deus.wallet.modules.send.tron.SendTronModule
import io.deus.wallet.modules.send.tron.SendTronScreen
import io.deus.wallet.modules.send.tron.SendTronViewModel
import io.deus.wallet.modules.send.zcash.SendZCashModule
import io.deus.wallet.modules.send.zcash.SendZCashScreen
import io.deus.wallet.modules.send.zcash.SendZCashViewModel
import io.deus.wallet.modules.sendtokenselect.PrefilledData
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize

class SendFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            try {
                val navController = findNavController()
                val input = navController.requireInput<Input>()
                val wallet = input.wallet
                val title = input.title
                val sendEntryPointDestId = input.sendEntryPointDestId
                val predefinedAddress = input.predefinedAddress
                val prefilledData = input.prefilledAddressData

                val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment) {
                    AmountInputModeModule.Factory(wallet.coin.uid)
                }

                when (wallet.token.blockchainType) {
                    BlockchainType.Bitcoin,
                    BlockchainType.BitcoinCash,
                    BlockchainType.ECash,
                    BlockchainType.Litecoin,
                    BlockchainType.Dash -> {
                        val factory = SendBitcoinModule.Factory(wallet, predefinedAddress)
                        val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendBitcoinNavHost(
                                title,
                                findNavController(),
                                sendBitcoinViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    is BlockchainType.BinanceChain -> {
                        val factory = SendBinanceModule.Factory(wallet, predefinedAddress)
                        val sendBinanceViewModel by navGraphViewModels<SendBinanceViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendBinanceScreen(
                                title,
                                findNavController(),
                                sendBinanceViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    BlockchainType.Zcash -> {
                        val factory = SendZCashModule.Factory(wallet, predefinedAddress)
                        val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment) {
                            factory
                        }
                        setContent {
                            SendZCashScreen(
                                title,
                                findNavController(),
                                sendZCashViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    BlockchainType.Ethereum,
                    BlockchainType.BinanceSmartChain,
                    BlockchainType.Polygon,
                    BlockchainType.Avalanche,
                    BlockchainType.Optimism,
                    BlockchainType.Gnosis,
                    BlockchainType.Fantom,
                    BlockchainType.ArbitrumOne -> {
                        val factory = SendEvmModule.Factory(wallet, predefinedAddress)
                        val evmKitWrapperViewModel by navGraphViewModels<EvmKitWrapperHoldingViewModel>(
                            R.id.sendXFragment
                        ) { factory }
                        @Suppress("UNUSED_VARIABLE")
                        val initiateLazyViewModel = evmKitWrapperViewModel //needed in SendEvmConfirmationFragment
                        val sendEvmViewModel by navGraphViewModels<SendEvmViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendEvmScreen(
                                title,
                                findNavController(),
                                sendEvmViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    BlockchainType.Solana -> {
                        val factory = SendSolanaModule.Factory(wallet, predefinedAddress)
                        val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendSolanaScreen(
                                title,
                                findNavController(),
                                sendSolanaViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    BlockchainType.Ton -> {
                        val factory = SendTonModule.Factory(wallet, predefinedAddress)
                        val sendTonViewModel by navGraphViewModels<SendTonViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendTonScreen(
                                title,
                                findNavController(),
                                sendTonViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    BlockchainType.Tron -> {
                        val factory = SendTronModule.Factory(wallet, predefinedAddress)
                        val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment) { factory }
                        setContent {
                            SendTronScreen(
                                title,
                                findNavController(),
                                sendTronViewModel,
                                amountInputModeViewModel,
                                sendEntryPointDestId,
                                prefilledData,
                            )
                        }
                    }

                    else -> {}
                }
            } catch (t: Throwable) {
                findNavController().popBackStack()
            }
        }
    }

    @Parcelize
    data class Input(
        val wallet: Wallet,
        val title: String,
        val sendEntryPointDestId: Int = 0,
        val predefinedAddress: String? = null,
        val prefilledAddressData: PrefilledData? = null
    ) : Parcelable
}
