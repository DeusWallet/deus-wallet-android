package io.deus.wallet.modules.enablecoin.blockchaintokens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.deus.wallet.R
import io.deus.wallet.core.description
import io.deus.wallet.core.imageUrl
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.core.subscribeIO
import io.deus.wallet.core.title
import io.deus.wallet.modules.market.ImageSource
import io.deus.wallet.ui.extensions.BottomSheetSelectorMultipleDialog
import io.deus.wallet.ui.extensions.BottomSheetSelectorViewItem
import io.reactivex.disposables.Disposable

class BlockchainTokensViewModel(
    private val service: BlockchainTokensService
) : ViewModel() {

    var showBottomSheetDialog by mutableStateOf(false)
        private set

    var config: BottomSheetSelectorMultipleDialog.Config? = null
        private set
    private var currentRequest: BlockchainTokensService.Request? = null
    private val disposable: Disposable

    init {
        disposable = service.requestObservable
            .subscribeIO {
                handle(it)
            }
    }

    private fun handle(request: BlockchainTokensService.Request) {
        currentRequest = request
        val blockchain = request.blockchain
        val selectedTokenIndexes = request.enabledTokens.map { request.tokens.indexOf(it) }

        val config = BottomSheetSelectorMultipleDialog.Config(
            icon = ImageSource.Remote(blockchain.type.imageUrl, R.drawable.ic_platform_placeholder_32),
            title = blockchain.name,
            description = Translator.getString(R.string.AddressFormatSettings_Description),
            selectedIndexes = selectedTokenIndexes,
            allowEmpty = request.allowEmpty,
            viewItems = request.tokens.map { token ->
                BottomSheetSelectorViewItem(
                    title = token.type.title,
                    subtitle = token.type.description,
                )
            }
        )
        showBottomSheetDialog = true
        this.config = config
    }

    fun bottomSheetDialogShown() {
        showBottomSheetDialog = false
    }

    fun onSelect(indexes: List<Int>) {
        currentRequest?.let { currentRequest ->
            service.select(indexes.map { currentRequest.tokens[it] }, currentRequest.blockchain)
        }
    }

    fun onCancelSelect() {
        currentRequest?.let { currentRequest ->
            service.cancel(currentRequest.blockchain)
        }
    }

}
