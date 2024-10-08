package io.deus.wallet.modules.send.solana

import io.deus.wallet.R
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import io.horizontalsystems.solanakit.models.Address as SolanaAddress

class SendSolanaAddressService(prefilledAddress: String?) {
    private var address: Address? = prefilledAddress?.let { Address(it) }
    private var addressError: Throwable? = null
    var solanaAddress: SolanaAddress? = prefilledAddress?.let { SolanaAddress(it) }
        private set

    private val _stateFlow = MutableStateFlow(
        State(
            address = address,
            evmAddress = solanaAddress,
            addressError = addressError,
            canBeSend = solanaAddress != null,
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun setAddress(address: Address?) {
        this.address = address

        validateAddress()

        emitState()
    }

    private fun validateAddress() {
        addressError = null
        solanaAddress = null
        val address = this.address ?: return

        try {
            solanaAddress = SolanaAddress(address.hex)
        } catch (e: Exception) {
            addressError = Throwable(Translator.getString(R.string.SwapSettings_Error_InvalidAddress))
        }
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                address = address,
                evmAddress = solanaAddress,
                addressError = addressError,
                canBeSend = solanaAddress != null
            )
        }
    }

    data class State(
        val address: Address?,
        val evmAddress: SolanaAddress?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )
}
