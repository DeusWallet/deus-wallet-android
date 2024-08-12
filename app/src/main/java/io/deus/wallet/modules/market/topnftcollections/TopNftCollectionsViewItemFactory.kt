package io.deus.wallet.modules.market.topnftcollections

import io.deus.wallet.core.IAppNumberFormatter
import io.deus.wallet.entities.CoinValue
import io.deus.wallet.modules.market.TimeDuration
import io.deus.wallet.modules.nft.NftCollectionItem
import java.math.BigDecimal

class TopNftCollectionsViewItemFactory(
    private val numberFormatter: IAppNumberFormatter
) {

    fun viewItem(
        collection: NftCollectionItem,
        timeDuration: TimeDuration,
        order: Int
    ): TopNftCollectionViewItem {
        val volume: CoinValue?
        val volumeDiff: BigDecimal?
        when (timeDuration) {
            TimeDuration.OneDay -> {
                volume = collection.oneDayVolume
                volumeDiff = collection.oneDayVolumeDiff
            }
            TimeDuration.SevenDay -> {
                volume = collection.sevenDayVolume
                volumeDiff = collection.sevenDayVolumeDiff
            }
            TimeDuration.ThirtyDay -> {
                volume = collection.thirtyDayVolume
                volumeDiff = collection.thirtyDayVolumeDiff
            }

            TimeDuration.ThreeMonths -> {
                volume = null
                volumeDiff = null
            }
        }
        val volumeFormatted = volume?.let { numberFormatter.formatCoinShort(it.value, it.coin.code, 2) } ?: "---"
        val floorPriceFormatted = collection.floorPrice?.let {
            "Floor: " + numberFormatter.formatCoinShort(it.value, it.coin.code, 2)
        } ?: "---"

        return TopNftCollectionViewItem(
            blockchainType = collection.blockchainType,
            uid = collection.uid,
            name = collection.name,
            imageUrl = collection.imageUrl,
            volume = volumeFormatted,
            volumeDiff = volumeDiff ?: BigDecimal.ZERO,
            order = order,
            floorPrice = floorPriceFormatted
        )
    }

}
