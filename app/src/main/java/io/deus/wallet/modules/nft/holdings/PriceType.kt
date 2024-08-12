package io.deus.wallet.modules.nft.holdings

import io.deus.wallet.R
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.WithTranslatableTitle

enum class PriceType(override val title: TranslatableString) : WithTranslatableTitle {
    LastSale(TranslatableString.ResString(R.string.Nfts_PriceType_LastSale)),
    Days7(TranslatableString.ResString(R.string.Nfts_PriceType_Days_7)),
    Days30(TranslatableString.ResString(R.string.Nfts_PriceType_Days_30))
}