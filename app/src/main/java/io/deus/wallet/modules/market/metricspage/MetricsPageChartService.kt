package io.deus.wallet.modules.market.metricspage

import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.stat
import io.deus.wallet.core.stats.statPage
import io.deus.wallet.core.stats.statPeriod
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.chart.AbstractChartService
import io.deus.wallet.modules.chart.ChartPointsWrapper
import io.deus.wallet.modules.market.tvl.GlobalMarketRepository
import io.deus.wallet.modules.metricchart.MetricsType
import io.horizontalsystems.chartview.ChartViewType
import io.horizontalsystems.marketkit.models.HsTimePeriod
import io.reactivex.Single

class MetricsPageChartService(
    override val currencyManager: CurrencyManager,
    private val metricsType: MetricsType,
    private val globalMarketRepository: GlobalMarketRepository,
) : AbstractChartService() {

    override val initialChartInterval: HsTimePeriod = HsTimePeriod.Day1

    override val chartIntervals = listOf(
        HsTimePeriod.Day1,
        HsTimePeriod.Week1,
        HsTimePeriod.Month1,
        HsTimePeriod.Month3,
        HsTimePeriod.Month6,
        HsTimePeriod.Year1,
        HsTimePeriod.Year2,
    )

    override val chartViewType = ChartViewType.Line

    override fun getItems(
        chartInterval: HsTimePeriod,
        currency: Currency,
    ): Single<ChartPointsWrapper> {
        return globalMarketRepository.getGlobalMarketPoints(
            currency.code,
            chartInterval,
            metricsType
        ).map {
            ChartPointsWrapper(it)
        }
    }

    override fun updateChartInterval(chartInterval: HsTimePeriod?) {
        super.updateChartInterval(chartInterval)

        stat(page = metricsType.statPage, event = StatEvent.SwitchChartPeriod(chartInterval.statPeriod))
    }
}
