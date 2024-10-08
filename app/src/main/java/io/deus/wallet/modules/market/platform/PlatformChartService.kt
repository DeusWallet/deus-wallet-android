package io.deus.wallet.modules.market.platform

import android.util.Log
import io.deus.wallet.core.managers.CurrencyManager
import io.deus.wallet.core.managers.MarketKitWrapper
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.core.stats.statPeriod
import io.deus.wallet.entities.Currency
import io.deus.wallet.modules.chart.AbstractChartService
import io.deus.wallet.modules.chart.ChartPointsWrapper
import io.deus.wallet.modules.market.topplatforms.Platform
import io.horizontalsystems.chartview.ChartViewType
import io.horizontalsystems.chartview.models.ChartPoint
import io.horizontalsystems.marketkit.models.HsPeriodType
import io.horizontalsystems.marketkit.models.HsTimePeriod
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import retrofit2.HttpException
import java.io.IOException

class PlatformChartService(
    private val platform: Platform,
    override val currencyManager: CurrencyManager,
    private val marketKit: MarketKitWrapper,
) : AbstractChartService() {

    override val initialChartInterval = HsTimePeriod.Week1
    override var chartIntervals = listOf<HsTimePeriod?>()
    override val chartViewType = ChartViewType.Line

    private var chartStartTime: Long = 0

    override suspend fun start() {
        try {
            chartStartTime = marketKit.topPlatformMarketCapStartTimeSingle(platform.uid).await()
        } catch (e: IOException) {
            Log.e("PlatformChartService", "start error: ", e)
        } catch (e: HttpException) {
            Log.e("PlatformChartService", "start error: ", e)
        }

        val now = System.currentTimeMillis() / 1000L
        val mostPeriodSeconds = now - chartStartTime

        chartIntervals = HsTimePeriod.values().filter {
            it.range <= mostPeriodSeconds
        } + listOf<HsTimePeriod?>(null)

        super.start()
    }

    override fun getAllItems(currency: Currency): Single<ChartPointsWrapper> {
        return getChartPointsWrapper(currency, HsPeriodType.ByStartTime(chartStartTime))
    }

    override fun getItems(
        chartInterval: HsTimePeriod,
        currency: Currency,
    ): Single<ChartPointsWrapper> {
        return getChartPointsWrapper(currency, HsPeriodType.ByPeriod(chartInterval))
    }

    override fun updateChartInterval(chartInterval: HsTimePeriod?) {
        super.updateChartInterval(chartInterval)

        stat(page = StatPage.TopPlatform, event = StatEvent.SwitchChartPeriod(chartInterval.statPeriod))
    }

    private fun getChartPointsWrapper(
        currency: Currency,
        periodType: HsPeriodType,
    ): Single<ChartPointsWrapper> {
        return try {
            marketKit.topPlatformMarketCapPointsSingle(platform.uid, currency.code, periodType)
                .map { info -> info.map { ChartPoint(it.marketCap.toFloat(), it.timestamp) } }
                .map { ChartPointsWrapper(it) }
        } catch (e: Exception) {
            Single.error(e)
        }
    }
}
