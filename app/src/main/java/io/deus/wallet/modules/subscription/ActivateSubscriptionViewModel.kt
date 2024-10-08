package io.deus.wallet.modules.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.IAccountManager
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.managers.SubscriptionManager
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.core.toHexString
import io.deus.wallet.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import java.net.UnknownHostException

class ActivateSubscriptionViewModel(
    private val marketKit: MarketKitWrapper,
    private val accountManager: IAccountManager,
    private val subscriptionManager: SubscriptionManager
) : ViewModelUiState<ActivateSubscription>() {

    private var subscriptionAccount: SubscriptionAccount? = null

    private var fetchingMessage = true
    private var fetchingMessageError: Throwable? = null
    private var subscriptionInfo: SubscriptionInfo? = null
    private var fetchingToken = false
    private var fetchingTokenError: Throwable? = null
    private var fetchingTokenSuccess = false

    init {
        fetchMessageToSign()
    }

    override fun createState() = ActivateSubscription(
        fetchingMessage = fetchingMessage,
        fetchingMessageError = fetchingMessageError,
        subscriptionInfo = subscriptionInfo,
        fetchingToken = fetchingToken,
        fetchingTokenError = fetchingTokenError,
        fetchingTokenSuccess = fetchingTokenSuccess,
        signButtonState = getSignButtonState()
    )

    private fun fetchMessageToSign() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchingMessage = true
            emitState()

            try {
                val accountsMap = accountManager.accounts.mapNotNull { account ->
                    account.type.evmAddress(App.evmBlockchainManager.getChain(BlockchainType.Ethereum))?.hex?.let { address ->
                        Pair(address, account)
                    }
                }.associateBy({ it.first }, { it.second })

                val addresses = accountsMap.keys.toList()
                val subscriptions = marketKit.subscriptionsSingle(addresses).await()
                val address = subscriptions.maxByOrNull { it.deadline }?.address ?: throw NoSubscription()

                subscriptionAccount = SubscriptionAccount(address, accountsMap[address])

                val messageToSign = marketKit.authGetSignMessage(address).await()

                fetchingMessage = false
                fetchingMessageError = null
                subscriptionInfo = SubscriptionInfo(
                    walletName = accountsMap[address]?.name ?: "--",
                    walletAddress = address,
                    messageToSign = messageToSign
                )

            } catch (e: Throwable) {
                fetchingMessage = false
                fetchingMessageError = if (e is UnknownHostException) {
                    IllegalStateException(Translator.getString(R.string.Hud_Text_NoInternet))
                } else {
                    e
                }
            }

            emitState()
        }
    }

    private fun getSignButtonState() = when {
        fetchingToken -> ButtonState.Disabled
        fetchingMessage -> ButtonState.Hidden
        subscriptionInfo != null -> ButtonState.Enabled
        else -> ButtonState.Hidden
    }

    fun sign() {
        val subscriptionInfo = subscriptionInfo ?: return
        val account = subscriptionAccount?.account ?: return
        val address = subscriptionAccount?.address ?: return

        fetchingToken = true
        emitState()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val signature = account.type.sign(subscriptionInfo.messageToSign.toByteArray()) ?: throw IllegalStateException()
                val token = marketKit.authenticate(signature.toHexString(), address).await()
                subscriptionManager.authToken = token
                fetchingTokenSuccess = true
            } catch (t: Throwable) {
                fetchingTokenError = t
            }

            fetchingToken = false
            emitState()
        }
    }

    fun retry() {
        fetchMessageToSign()
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ActivateSubscriptionViewModel(
                App.marketKit,
                App.accountManager,
                App.subscriptionManager
            ) as T
        }
    }
}

data class SubscriptionAccount(
    val address: String,
    val account: Account?
)

data class SubscriptionInfo(
    val walletName: String,
    val walletAddress: String,
    val messageToSign: String
)

data class ActivateSubscription(
    val fetchingMessage: Boolean,
    val fetchingMessageError: Throwable?,
    val subscriptionInfo: SubscriptionInfo?,
    val fetchingToken: Boolean,
    val fetchingTokenError: Throwable?,
    val fetchingTokenSuccess: Boolean,
    val signButtonState: ButtonState
)

enum class ButtonState(val visible: Boolean, val enabled: Boolean) {
    Enabled(true, true), Disabled(true, false), Hidden(false, true)
}

class NoSubscription : Exception()
