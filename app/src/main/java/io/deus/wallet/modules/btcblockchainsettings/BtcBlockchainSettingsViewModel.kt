package io.deus.wallet.modules.btcblockchainsettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.deus.wallet.R
import io.deus.wallet.core.imageUrl
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.BtcRestoreMode
import io.deus.wallet.modules.btcblockchainsettings.BtcBlockchainSettingsModule.BlockchainSettingsIcon
import io.deus.wallet.modules.btcblockchainsettings.BtcBlockchainSettingsModule.ViewItem
import io.reactivex.disposables.CompositeDisposable

class BtcBlockchainSettingsViewModel(
    private val service: BtcBlockchainSettingsService
) : ViewModel() {

    private val disposables = CompositeDisposable()

    var closeScreen by mutableStateOf(false)
        private set

    var restoreSources by mutableStateOf<List<ViewItem>>(listOf())
        private set

    var saveButtonEnabled by mutableStateOf(false)
        private set

    val title: String = service.blockchain.name
    val blockchainIconUrl = service.blockchain.type.imageUrl

    init {
        service.hasChangesObservable
            .subscribe {
                saveButtonEnabled = it
                syncRestoreModeState()
            }.let {
                disposables.add(it)
            }

        syncRestoreModeState()
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun onSelectRestoreMode(viewItem: ViewItem) {
        service.setRestoreMode(viewItem.id)
    }

    fun onSaveClick() {
        service.save()
        closeScreen = true
    }

    private fun syncRestoreModeState() {
        val viewItems = service.restoreModes.map { mode ->
            ViewItem(
                id = mode.raw,
                title = Translator.getString(mode.title),
                subtitle = Translator.getString(mode.description),
                selected = mode == service.restoreMode,
                icon = mode.icon
            )
        }
        restoreSources = viewItems
    }

    private val BtcRestoreMode.icon: BlockchainSettingsIcon
        get() = when (this) {
            BtcRestoreMode.Blockchair -> BlockchainSettingsIcon.ApiIcon(R.drawable.ic_blockchair)
            BtcRestoreMode.Hybrid -> BlockchainSettingsIcon.ApiIcon(R.drawable.ic_api_hybrid)
            BtcRestoreMode.Blockchain -> BlockchainSettingsIcon.BlockchainIcon(service.blockchain.type.imageUrl)
        }

}
