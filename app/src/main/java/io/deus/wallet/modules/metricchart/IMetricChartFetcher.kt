package io.deus.wallet.modules.metricchart

import io.deus.wallet.ui.compose.TranslatableString

interface IMetricChartFetcher {
    val title: Int
    val description: TranslatableString
    val poweredBy: TranslatableString
}