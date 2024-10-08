package io.deus.wallet.modules.receive.viewmodels

import androidx.lifecycle.viewModelScope
import io.deus.wallet.R
import io.deus.wallet.core.IAdapterManager
import io.deus.wallet.core.UsedAddress
import io.deus.wallet.core.ViewModelUiState
import io.deus.wallet.core.accountTypeDerivation
import io.deus.wallet.core.bitcoinCashCoinType
import io.deus.wallet.core.factories.uriScheme
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.core.utils.AddressUriParser
import io.deus.wallet.entities.AddressUri
import io.deus.wallet.entities.ViewState
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.receive.ReceiveModule
import io.deus.wallet.modules.receive.ReceiveModule.AdditionalData
import io.horizontalsystems.marketkit.models.TokenType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import java.math.BigDecimal

class ReceiveAddressViewModel(
    private val wallet: Wallet,
    private val adapterManager: IAdapterManager
) : ViewModelUiState<ReceiveModule.UiState>() {

    private var viewState: ViewState = ViewState.Loading
    private var address = ""
    private var usedAddresses: List<UsedAddress> = listOf()
    private var usedChangeAddresses: List<UsedAddress> = listOf()
    private var uri = ""
    private var amount: BigDecimal? = null
    private var accountActive = true
    private var networkName = ""
    private var mainNet = true
    private var watchAccount = wallet.account.isWatchAccount
    private var alertText: ReceiveModule.AlertText? = getAlertText(watchAccount)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            adapterManager.adaptersReadyObservable.asFlow()
                .collect {
                    setData()
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            setData()
        }
        setNetworkName()
    }

    override fun createState() = ReceiveModule.UiState(
        viewState = viewState,
        address = address,
        usedAddresses = usedAddresses,
        usedChangeAddresses = usedChangeAddresses,
        uri = uri,
        networkName = networkName,
        watchAccount = watchAccount,
        additionalItems = getAdditionalData(),
        amount = amount,
        alertText = alertText,
    )

    private fun setNetworkName() {
        when (val tokenType = wallet.token.type) {
            is TokenType.Derived -> {
                networkName = Translator.getString(R.string.Balance_Format) + ": "
                networkName += "${tokenType.derivation.accountTypeDerivation.addressType} (${tokenType.derivation.accountTypeDerivation.rawName})"
            }

            is TokenType.AddressTyped -> {
                networkName = Translator.getString(R.string.Balance_Format) + ": "
                networkName += tokenType.type.bitcoinCashCoinType.title
            }

            else -> {
                networkName = Translator.getString(R.string.Balance_Network) + ": "
                networkName += wallet.token.blockchain.name
            }
        }
        if (!mainNet) {
            networkName += " (TestNet)"
        }
        emitState()
    }

    private fun getAlertText(watchAccount: Boolean): ReceiveModule.AlertText? {
        return if (watchAccount) ReceiveModule.AlertText.Normal(
            Translator.getString(R.string.Balance_Receive_WatchAddressAlert)
        )
        else null
    }

    private suspend fun setData() {
        val adapter = adapterManager.getReceiveAdapterForWallet(wallet)
        if (adapter != null) {
            address = adapter.receiveAddress
            usedAddresses = adapter.usedAddresses(false)
            usedChangeAddresses = adapter.usedAddresses(true)
            uri = getUri()
            accountActive = adapter.isAddressActive(adapter.receiveAddress)
            mainNet = adapter.isMainNet
            viewState = ViewState.Success
        } else {
            viewState = ViewState.Error(NullPointerException())
        }
        emitState()
    }

    private fun getUri(): String {
        var newUri = address
        amount?.let {
            val parser = AddressUriParser(wallet.token.blockchainType, wallet.token.type)
            val addressUri = AddressUri(wallet.token.blockchainType.uriScheme ?: "")
            addressUri.address = newUri
            addressUri.parameters[AddressUri.Field.amountField(wallet.token.blockchainType)] = it.toString()
            addressUri.parameters[AddressUri.Field.BlockchainUid] = wallet.token.blockchainType.uid
            if (wallet.token.type !is TokenType.Derived && wallet.token.type !is TokenType.AddressTyped) {
                addressUri.parameters[AddressUri.Field.TokenUid] = wallet.token.type.id
            }
            newUri = parser.uri(addressUri)
        }

        return newUri
    }

    private fun getAdditionalData(): List<AdditionalData> {
        val items = mutableListOf<AdditionalData>()

        if (!accountActive) {
            items.add(AdditionalData.AccountNotActive)
        }

        amount?.let {
            items.add(
                AdditionalData.Amount(
                    value = it.toString()
                )
            )
        }

        return items
    }

    fun onErrorClick() {
        viewModelScope.launch(Dispatchers.IO) {
            setData()
        }
    }

    fun setAmount(amount: BigDecimal?) {
        amount?.let {
            if (it <= BigDecimal.ZERO) {
                this.amount = null
                emitState()
                return
            }
        }
        this.amount = amount
        uri = getUri()
        emitState()
    }

}
