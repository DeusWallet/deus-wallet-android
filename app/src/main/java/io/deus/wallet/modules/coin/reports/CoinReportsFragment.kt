package io.deus.wallet.modules.coin.reports

import android.os.Parcelable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.requireInput
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.overview.ui.Loading
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.HSSwipeRefresh
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellNews
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.ListErrorView
import io.deus.wallet.ui.helpers.LinkHelper
import kotlinx.parcelize.Parcelize

class CoinReportsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.requireInput<Input>()

        CoinReportsScreen(
            viewModel = viewModel(factory = CoinReportsModule.Factory(input.coinUid)),
            onClickNavigation = {
                navController.popBackStack()
            },
            onClickReportUrl = {
                LinkHelper.openLinkInAppBrowser(requireContext(), it)
            }
        )
    }

    @Parcelize
    data class Input(val coinUid: String) : Parcelable
}

@Composable
private fun CoinReportsScreen(
    viewModel: CoinReportsViewModel,
    onClickNavigation: () -> Unit,
    onClickReportUrl: (url: String) -> Unit
) {
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val reportViewItems by viewModel.reportViewItemsLiveData.observeAsState()

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.CoinPage_Reports),
            navigationIcon = {
                HsBackButton(onClick = onClickNavigation)
            }
        )
        HSSwipeRefresh(
            refreshing = isRefreshing,
            onRefresh = viewModel::refresh
        ) {
            Crossfade(viewState) { viewState ->
                when (viewState) {
                    ViewState.Loading -> {
                        Loading()
                    }

                    is ViewState.Error -> {
                        ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                    }

                    ViewState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            reportViewItems?.let {
                                items(it) { report ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CellNews(
                                        source = report.author,
                                        title = report.title,
                                        body = report.body,
                                        date = report.date,
                                    ) {
                                        onClickReportUrl(report.url)
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    null -> {}
                }
            }
        }
    }
}
