package io.deus.wallet.modules.chart

import io.deus.wallet.core.App
import io.deus.wallet.entities.Currency
import io.horizontalsystems.marketkit.models.FullCoin
import java.math.BigDecimal

class ChartCoinValueFormatterShortened(private val fullCoin: FullCoin) : ChartModule.ChartNumberFormatter {

    override fun formatValue(currency: Currency, value: BigDecimal): String {
        return App.numberFormatter.formatCoinShort(value, fullCoin.coin.code, 8)
    }

}
