package io.deus.wallet.modules.coin.indicators

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.modules.chart.ChartIndicatorSetting
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.parcelize.Parcelize

class IndicatorSettingsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val indicatorSetting = navController.getInput<Input>()?.indicatorId?.let {
            App.chartIndicatorManager.getChartIndicatorSetting(it)
        }

        if (indicatorSetting == null) {
            HudHelper.showErrorMessage(LocalView.current, R.string.Error_ParameterNotSet)
            navController.popBackStack()
        } else {
            when (indicatorSetting.type) {
                ChartIndicatorSetting.IndicatorType.MA -> {
                    EmaSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }

                ChartIndicatorSetting.IndicatorType.RSI -> {
                    RsiSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }

                ChartIndicatorSetting.IndicatorType.MACD -> {
                    MacdSettingsScreen(
                        navController = navController,
                        indicatorSetting = indicatorSetting
                    )
                }
            }
        }
    }

    @Parcelize
    data class Input(val indicatorId: String) : Parcelable
}
