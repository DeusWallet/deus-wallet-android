package io.deus.wallet.modules.depositcex

import io.deus.wallet.core.providers.CexAsset
import io.deus.wallet.modules.market.ImageSource

object DepositCexModule {

    data class CexCoinViewItem(
        val title: String,
        val subtitle: String,
        val coinIconUrl: String?,
        val coinIconPlaceholder: Int,
        val cexAsset: CexAsset,
        val depositEnabled: Boolean,
        val withdrawEnabled: Boolean,
    )

    data class NetworkViewItem(
        val title: String,
        val imageSource: ImageSource,
    )

}
