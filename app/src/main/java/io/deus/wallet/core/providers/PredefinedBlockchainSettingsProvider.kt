package io.deus.wallet.core.providers

import io.deus.wallet.core.managers.RestoreSettings
import io.deus.wallet.core.managers.RestoreSettingsManager
import io.deus.wallet.core.managers.ZcashBirthdayProvider
import io.deus.wallet.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType

class PredefinedBlockchainSettingsProvider(
    private val manager: RestoreSettingsManager,
    private val zcashBirthdayProvider: ZcashBirthdayProvider
) {

    fun prepareNew(account: Account, blockchainType: BlockchainType) {
        val settings = RestoreSettings()
        when (blockchainType) {
            BlockchainType.Zcash -> {
                settings.birthdayHeight = zcashBirthdayProvider.getLatestCheckpointBlockHeight()
            }
            else -> {}
        }
        if (settings.isNotEmpty()) {
            manager.save(settings, account, blockchainType)
        }
    }
}
