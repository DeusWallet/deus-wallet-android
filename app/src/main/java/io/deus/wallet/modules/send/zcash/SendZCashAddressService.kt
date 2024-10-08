package io.deus.wallet.modules.send.zcash

import io.deus.wallet.R
import io.deus.wallet.core.ISendZcashAdapter
import io.deus.wallet.core.adapters.zcash.ZcashAdapter
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.entities.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SendZCashAddressService(private val adapter: ISendZcashAdapter, prefilledAddress: String?) {
    var address: Address? = prefilledAddress?.let { Address(it) }
        private set
    private var addressType: ZcashAdapter.ZCashAddressType? = null
    private var addressError: Throwable? = null

    private val _stateFlow = MutableStateFlow(
        State(
            address = address,
            addressType = addressType,
            addressError = addressError,
            canBeSend = address != null && addressError == null
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun setAddress(address: Address?) {
        this.address = address

        validateAddress()

        emitState()
    }

    private suspend fun validateAddress() {
        addressType = null
        addressError = null
        val address = this.address ?: return

        try {
            addressType = adapter.validate(address.hex)
        } catch (e: Exception) {
            addressError = getError(e)
        }
    }

    private fun getError(error: Throwable): Throwable {
        val message = when (error) {
            is ZcashAdapter.ZcashError.SendToSelfNotAllowed -> Translator.getString(R.string.Send_Error_SendToSelf)
            is ZcashAdapter.ZcashError.InvalidAddress -> Translator.getString(R.string.SwapSettings_Error_InvalidAddress)
            else -> error.message ?: error.javaClass.simpleName
        }

        return Throwable(message)
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                address = address,
                addressType = addressType,
                addressError = addressError,
                canBeSend = address != null && addressError == null
            )
        }
    }

    data class State(
        val address: Address?,
        val addressType: ZcashAdapter.ZCashAddressType?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )
}
