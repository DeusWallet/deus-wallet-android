package io.deus.wallet.modules.metricchart

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import io.deus.wallet.R
import io.deus.wallet.core.getInputX
import io.deus.wallet.modules.chart.ChartViewModel
import io.deus.wallet.modules.coin.overview.ui.Chart
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.extensions.BaseComposableBottomSheetFragment
import io.deus.wallet.ui.extensions.BottomSheetHeader
import kotlinx.parcelize.Parcelize

class ProChartFragment : BaseComposableBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                val input = requireArguments().getInputX<Input>()!!
                val chartViewModel = viewModel<ChartViewModel>(
                    factory = ProChartModule.Factory(
                        input.coinUid,
                        enumValues<ProChartModule.ChartType>()[input.chartType]
                    )
                )

                ComposeAppTheme {
                    BottomSheetHeader(
                        iconPainter = painterResource(R.drawable.ic_chart_24),
                        iconTint = ColorFilter.tint(ComposeAppTheme.colors.jacob),
                        title = input.title,
                        onCloseClick = { close() }
                    ) {
                        Chart(chartViewModel = chartViewModel)
                        VSpacer(32.dp)
                    }
                }
            }
        }
    }

    @Parcelize
    data class Input(
        val coinUid: String,
        val title: String,
        val chartType: Int,
    ) : Parcelable

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            coinUid: String,
            title: String,
            chartType: ProChartModule.ChartType,
        ) {
            val fragment = ProChartFragment()
            fragment.arguments = bundleOf(
                "input" to Input(coinUid, title, chartType.ordinal)
            )
            fragment.show(fragmentManager, "pro_chart_dialog")
        }
    }
}

