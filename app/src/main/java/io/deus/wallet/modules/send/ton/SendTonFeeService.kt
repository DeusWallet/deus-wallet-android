package io.deus.wallet.modules.send.ton

import io.deus.wallet.core.ISendTonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class SendTonFeeService(private val adapter: ISendTonAdapter) {

    private var fee: BigDecimal? = null
    private val _stateFlow = MutableStateFlow(
        State(
            fee = fee
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    suspend fun start() = withContext(Dispatchers.IO) {
        fee = adapter.estimateFee()

        emitState()
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                fee = fee
            )
        }
    }


    data class State(
        val fee: BigDecimal?
    )
}
