package io.deus.wallet.modules.multiswap.providers

import io.deus.wallet.R
import io.horizontalsystems.marketkit.models.BlockchainType

object QuickSwapProvider : BaseUniswapProvider() {
    override val id = "quickswap"
    override val title = "QuickSwap"
    override val url = "https://quickswap.exchange/"
    override val icon = R.drawable.quickswap

    override fun supports(blockchainType: BlockchainType): Boolean {
        return blockchainType == BlockchainType.Polygon
    }
}
