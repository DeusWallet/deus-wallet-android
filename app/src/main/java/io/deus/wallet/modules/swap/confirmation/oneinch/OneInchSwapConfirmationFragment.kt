package io.deus.wallet.modules.swap.confirmation.oneinch

import android.os.Parcelable
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.AppLogger
import io.deus.wallet.core.getInputX
import io.deus.wallet.modules.evmfee.EvmFeeCellViewModel
import io.deus.wallet.modules.send.evm.settings.SendEvmNonceViewModel
import io.deus.wallet.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.deus.wallet.modules.swap.SwapMainModule.OneInchSwapParameters
import io.deus.wallet.modules.swap.confirmation.BaseSwapConfirmationFragment
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize

class OneInchSwapConfirmationFragment(
    override val navGraphId: Int = R.id.oneInchConfirmationFragment
) : BaseSwapConfirmationFragment() {

    private val input by lazy {
        requireArguments().getInputX<Input>()!!
    }

    override val logger = AppLogger("swap_1inch")

    private val vmFactory by lazy {
        OneInchConfirmationModule.Factory(input.blockchainType, input.oneInchSwapParameters)
    }

    override val swapEntryPointDestId: Int
        get() = input.swapEntryPointDestId
    override val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(navGraphId) { vmFactory }
    override val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(navGraphId) { vmFactory }
    override val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(navGraphId) { vmFactory }

    @Parcelize
    data class Input(
        val blockchainType: BlockchainType,
        val oneInchSwapParameters: OneInchSwapParameters,
        val swapEntryPointDestId: Int,
    ) : Parcelable
}
