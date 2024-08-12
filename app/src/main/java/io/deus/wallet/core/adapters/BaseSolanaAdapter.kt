package io.deus.wallet.core.adapters

import io.deus.wallet.core.IAdapter
import io.deus.wallet.core.IBalanceAdapter
import io.deus.wallet.core.IReceiveAdapter
import io.deus.wallet.core.managers.SolanaKitWrapper
import io.horizontalsystems.solanakit.Signer

abstract class BaseSolanaAdapter(
        solanaKitWrapper: SolanaKitWrapper,
        val decimal: Int
) : IAdapter, IBalanceAdapter, IReceiveAdapter {

    val solanaKit = solanaKitWrapper.solanaKit
    protected val signer: Signer? = solanaKitWrapper.signer

    override val debugInfo: String
        get() = solanaKit.debugInfo()

    val statusInfo: Map<String, Any>
        get() = solanaKit.statusInfo()

    // IReceiveAdapter

    override val receiveAddress: String
        get() = solanaKit.receiveAddress

    override val isMainNet: Boolean
        get() = solanaKit.isMainnet

    companion object {
        const val confirmationsThreshold: Int = 12
    }

}
