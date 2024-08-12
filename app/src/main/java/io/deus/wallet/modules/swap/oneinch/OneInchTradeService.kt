package io.deus.wallet.modules.swap.oneinch

import io.deus.wallet.core.subscribeIO
import io.deus.wallet.entities.Address
import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.SwapMainModule.ExactType
import io.deus.wallet.modules.swap.SwapMainModule.OneInchSwapParameters
import io.deus.wallet.modules.swap.SwapMainModule.SwapData
import io.deus.wallet.modules.swap.SwapMainModule.SwapResultState
import io.deus.wallet.modules.swap.settings.oneinch.OneInchSwapSettingsModule
import io.deus.wallet.modules.swap.settings.oneinch.OneInchSwapSettingsModule.OneInchSwapSettings
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.oneinchkit.Quote
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class OneInchTradeService(
    private val oneInchKitHelper: OneInchKitHelper
) : SwapMainModule.ISwapTradeService {

    private var quoteDisposable: Disposable? = null

    override var state: SwapResultState = SwapResultState.NotReady()
        private set(value) {
            field = value
            _stateFlow.update { value }
        }

    override val recipient: Address?
        get() = swapSettings.recipient

    override val slippage: BigDecimal
        get() = swapSettings.slippage

    private val _stateFlow = MutableStateFlow(state)
    override val stateFlow: StateFlow<SwapResultState>
        get() = _stateFlow

    private var swapSettings = OneInchSwapSettings()

    override fun stop() {
        clearDisposables()
    }

    fun onCleared() {
        clearDisposables()
    }

    private fun clearDisposables() {
        quoteDisposable?.dispose()
        quoteDisposable = null
    }

    override fun fetchSwapData(
        tokenFrom: Token?,
        tokenTo: Token?,
        amountFrom: BigDecimal?,
        amountTo: BigDecimal?,
        exactType: ExactType
    ) {
        quoteDisposable?.dispose()
        quoteDisposable = null

        val fromToken = tokenFrom ?: return
        val toToken = tokenTo ?: return

        if (amountFrom == null || amountFrom.compareTo(BigDecimal.ZERO) == 0) {
            state = SwapResultState.NotReady()
            return
        }

        state = SwapResultState.Loading

        quoteDisposable = oneInchKitHelper.getQuoteAsync(fromToken, toToken, amountFrom)
            .subscribeIO({ quote ->
                handle(quote, tokenFrom, tokenTo, amountFrom)
            }, { error ->
                state = SwapResultState.NotReady(listOf(error))
            })
    }

    override fun updateSwapSettings(recipient: Address?, slippage: BigDecimal?, ttl: Long?) {
        swapSettings = OneInchSwapSettings(
            recipient = recipient,
            slippage = slippage ?: OneInchSwapSettingsModule.defaultSlippage
        )
    }

    private fun handle(quote: Quote, tokenFrom: Token, tokenTo: Token, amountFrom: BigDecimal) {
        val amountToBigDecimal = quote.toTokenAmount.abs().toBigDecimal().movePointLeft(quote.toToken.decimals).stripTrailingZeros()

        val parameters = OneInchSwapParameters(
            tokenFrom,
            tokenTo,
            amountFrom,
            amountToBigDecimal,
            swapSettings.slippage,
            swapSettings.recipient
        )

        state = SwapResultState.Ready(SwapData.OneInchData(parameters))
    }

}
