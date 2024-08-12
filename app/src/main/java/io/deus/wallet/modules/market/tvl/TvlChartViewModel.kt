package io.deus.wallet.modules.market.tvl

import io.deus.wallet.modules.chart.ChartCurrencyValueFormatterShortened
import io.deus.wallet.modules.chart.ChartViewModel

class TvlChartViewModel(
    private val tvlChartService: TvlChartService,
    chartCurrencyValueFormatter: ChartCurrencyValueFormatterShortened,
) : ChartViewModel(tvlChartService, chartCurrencyValueFormatter) {

    fun onSelectChain(chain: TvlModule.Chain) {
        tvlChartService.chain = chain
    }

}
