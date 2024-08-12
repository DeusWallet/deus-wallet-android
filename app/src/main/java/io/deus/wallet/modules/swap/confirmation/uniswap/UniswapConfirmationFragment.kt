package io.deus.wallet.modules.swap.confirmation.uniswap

import android.os.Parcelable
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.AppLogger
import io.deus.wallet.core.getInputX
import io.deus.wallet.modules.evmfee.EvmFeeCellViewModel
import io.deus.wallet.modules.send.evm.SendEvmData
import io.deus.wallet.modules.send.evm.SendEvmModule
import io.deus.wallet.modules.send.evm.settings.SendEvmNonceViewModel
import io.deus.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import kotlinx.parcelize.Parcelize

class UniswapConfirmationFragment(
    override val navGraphId: Int = R.id.uniswapConfirmationFragment
) : BaseSwapConfirmationFragment() {

    private val input by lazy {
        requireArguments().getInputX<Input>()!!
    }

    override val logger = AppLogger("swap_uniswap")

    private val vmFactory by lazy {
        UniswapConfirmationModule.Factory(
            input.dex,
            input.transactionData,
            input.additionalInfo
        )
    }

    override val swapEntryPointDestId: Int
        get() = input.swapEntryPointDestId
    override val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(navGraphId) { vmFactory }
    override val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(navGraphId) { vmFactory }
    override val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(navGraphId) { vmFactory }

    @Parcelize
    data class Input(
        val dex: SwapMainModule.Dex,
        val transactionDataParcelable: SendEvmModule.TransactionDataParcelable,
        val additionalInfo: SendEvmData.AdditionalInfo?,
        val swapEntryPointDestId: Int
    ) : Parcelable {
        val transactionData: TransactionData
            get() = TransactionData(
                Address(transactionDataParcelable.toAddress),
                transactionDataParcelable.value,
                transactionDataParcelable.input
            )
    }
}
