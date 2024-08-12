package io.deus.wallet.modules.swap.uniswap

import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.UniversalSwapTradeData
import io.deus.wallet.modules.swap.settings.uniswap.SwapTradeOptions
import io.horizontalsystems.ethereumkit.models.TransactionData

interface IUniswapTradeService : SwapMainModule.ISwapTradeService {
    var tradeOptions: SwapTradeOptions
    @Throws
    fun transactionData(tradeData: UniversalSwapTradeData): TransactionData
}