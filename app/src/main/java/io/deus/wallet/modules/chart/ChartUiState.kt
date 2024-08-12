package io.deus.wallet.modules.chart

import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.ChartInfoData
import io.deus.wallet.ui.compose.components.TabItem
import io.horizontalsystems.chartview.ChartViewType
import io.horizontalsystems.marketkit.models.HsTimePeriod

data class ChartUiState(
    val tabItems: List<TabItem<HsTimePeriod?>>,
    val chartHeaderView: ChartModule.ChartHeaderView?,
    val chartInfoData: ChartInfoData?,
    val loading: Boolean,
    val viewState: ViewState,
    val hasVolumes: Boolean,
    val chartViewType: ChartViewType,
)
