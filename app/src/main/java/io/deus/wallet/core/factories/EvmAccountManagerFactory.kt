package io.deus.wallet.core.factories

import io.deus.wallet.core.IAccountManager
import io.deus.wallet.core.IWalletManager
import io.deus.wallet.core.managers.EvmAccountManager
import io.deus.wallet.core.managers.EvmKitManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.managers.TokenAutoEnableManager
import io.horizontalsystems.marketkit.models.BlockchainType

class EvmAccountManagerFactory(
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
    private val tokenAutoEnableManager: TokenAutoEnableManager
) {

    fun evmAccountManager(blockchainType: BlockchainType, evmKitManager: EvmKitManager) =
        EvmAccountManager(
            blockchainType,
            accountManager,
            walletManager,
            marketKit,
            evmKitManager,
            tokenAutoEnableManager
        )

}
