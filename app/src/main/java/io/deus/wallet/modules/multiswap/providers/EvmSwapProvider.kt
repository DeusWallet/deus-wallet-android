package io.deus.wallet.modules.multiswap.providers

import io.deus.wallet.core.App
import io.deus.wallet.core.adapters.Eip20Adapter
import io.deus.wallet.entities.transactionrecords.evm.ApproveTransactionRecord
import io.deus.wallet.modules.multiswap.action.ActionApprove
import io.deus.wallet.modules.multiswap.action.ActionRevoke
import io.deus.wallet.modules.multiswap.action.ISwapProviderAction
import io.deus.wallet.modules.send.evm.SendEvmData
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.DefaultBlockParameter
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.marketkit.models.TokenType
import kotlinx.coroutines.rx2.await
import java.math.BigDecimal
import java.math.BigInteger

abstract class EvmSwapProvider : IMultiSwapProvider {
    protected suspend fun getAllowance(token: Token, spenderAddress: Address): BigDecimal? {
        if (token.type !is TokenType.Eip20) return null

        val eip20Adapter = App.adapterManager.getAdapterForToken(token)

        if (eip20Adapter !is Eip20Adapter) return null

        return eip20Adapter.allowance(spenderAddress, DefaultBlockParameter.Latest).await()
    }

    protected fun actionApprove(
        allowance: BigDecimal?,
        amountIn: BigDecimal,
        routerAddress: Address,
        token: Token,
    ): ISwapProviderAction? {
        if (allowance == null || allowance >= amountIn) return null
        val eip20Adapter = App.adapterManager.getAdapterForToken(token)
        if (eip20Adapter !is Eip20Adapter) return null

        val approveTransaction = eip20Adapter.pendingTransactions
            .filterIsInstance<ApproveTransactionRecord>()
            .filter { it.spender.equals(routerAddress.eip55, true) }
            .maxByOrNull { it.timestamp }

        val revoke = allowance > BigDecimal.ZERO && isUsdt(token)

        return if (revoke) {
            val sendEvmData = SendEvmData(
                eip20Adapter.eip20Kit.buildApproveTransactionData(routerAddress, BigInteger.ZERO)
            )

            val revokeInProgress = approveTransaction != null && approveTransaction.value.zeroValue
            ActionRevoke(
                token,
                sendEvmData,
                revokeInProgress,
                allowance
            )
        } else {
            val approveInProgress = approveTransaction != null && !approveTransaction.value.zeroValue
            ActionApprove(
                amountIn,
                routerAddress,
                token,
                allowance,
                approveInProgress
            )
        }
    }

    private fun isUsdt(token: Token): Boolean {
        val tokenType = token.type

        return token.blockchainType is BlockchainType.Ethereum
            && tokenType is TokenType.Eip20
            && tokenType.address.lowercase() == "0xdac17f958d2ee523a2206206994597c13d831ec7"
    }

}
