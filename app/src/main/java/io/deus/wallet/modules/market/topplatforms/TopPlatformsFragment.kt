package io.deus.wallet.modules.market.topplatforms

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.core.stats.statPeriod
import io.deus.wallet.core.stats.statSortType
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.overview.ui.Loading
import io.deus.wallet.modules.market.ImageSource
import io.deus.wallet.modules.market.MarketDataValue
import io.deus.wallet.modules.market.SortingField
import io.deus.wallet.modules.market.TimeDuration
import io.deus.wallet.modules.market.topcoins.SelectorDialogState
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.HSSwipeRefresh
import io.deus.wallet.ui.compose.Select
import io.deus.wallet.ui.compose.components.AlertGroup
import io.deus.wallet.ui.compose.components.BadgeWithDiff
import io.deus.wallet.ui.compose.components.ButtonSecondaryToggle
import io.deus.wallet.ui.compose.components.CoinImage
import io.deus.wallet.ui.compose.components.DescriptionCard
import io.deus.wallet.ui.compose.components.HeaderSorting
import io.deus.wallet.ui.compose.components.ListErrorView
import io.deus.wallet.ui.compose.components.MarketCoinFirstRow
import io.deus.wallet.ui.compose.components.MarketDataValueComponent
import io.deus.wallet.ui.compose.components.SectionItemBorderedRowUniversalClear
import io.deus.wallet.ui.compose.components.SortMenu
import io.deus.wallet.ui.compose.components.TopCloseButton
import io.deus.wallet.ui.compose.components.subhead2_grey
import java.math.BigDecimal

class TopPlatformsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = viewModel<TopPlatformsViewModel>(
            factory = TopPlatformsModule.Factory(navController.getInput())
        )

        TopPlatformsScreen(
            viewModel,
            navController,
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPlatformsScreen(
    viewModel: TopPlatformsViewModel,
    navController: NavController,
) {

    val interactionSource = remember { MutableInteractionSource() }

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            TopCloseButton { navController.popBackStack() }

            HSSwipeRefresh(
                refreshing = viewModel.isRefreshing,
                onRefresh = {
                    viewModel.refresh()
                }
            ) {
                Crossfade(viewModel.viewState) { state ->
                    when (state) {
                        ViewState.Loading -> {
                            Loading()
                        }

                        is ViewState.Error -> {
                            ListErrorView(
                                stringResource(R.string.SyncError),
                                viewModel::onErrorClick
                            )
                        }

                        ViewState.Success -> {
                            viewModel.viewItems.let { viewItems ->
                                TopPlatformsList(
                                    viewItems = viewItems,
                                    sortingField = viewModel.sortingField,
                                    timeDuration = viewModel.timePeriod,
                                    onItemClick = {
                                        navController.slideFromRight(
                                            R.id.marketPlatformFragment,
                                            it
                                        )

                                        stat(page = StatPage.TopPlatforms, event = StatEvent.OpenPlatform(it.uid))
                                    },
                                    preItems = {
                                        item {
                                            DescriptionCard(
                                                stringResource(R.string.MarketTopPlatforms_PlatofrmsRank),
                                                stringResource(R.string.MarketTopPlatforms_Description),
                                                ImageSource.Local(R.drawable.ic_platforms)
                                            )
                                        }

                                        stickyHeader {
                                            var timePeriodMenu by remember {
                                                mutableStateOf(viewModel.timePeriodSelect)
                                            }

                                            HeaderSorting(borderTop = true, borderBottom = true) {
                                                SortMenu(
                                                    viewModel.sortingSelect.selected.titleResId,
                                                    viewModel::showSelectorMenu
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                ButtonSecondaryToggle(
                                                    select = timePeriodMenu,
                                                    onSelect = {
                                                        viewModel.onTimePeriodSelect(it)
                                                        timePeriodMenu = Select(
                                                            it,
                                                            viewModel.periodOptions
                                                        )

                                                        stat(page = StatPage.TopPlatforms, event = StatEvent.SwitchPeriod(it.statPeriod))
                                                    }
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        //Dialog
        when (val option = viewModel.selectorDialogState) {
            is SelectorDialogState.Opened -> {
                AlertGroup(
                    R.string.Market_Sort_PopupTitle,
                    option.select,
                    { selected ->
                        viewModel.onSelectSortingField(selected)

                        stat(page = StatPage.TopPlatforms, event = StatEvent.SwitchSortType(selected.statSortType))
                    },
                    { viewModel.onSelectorDialogDismiss() }
                )
            }

            SelectorDialogState.Closed -> {}
        }
    }
}

@Composable
private fun TopPlatformsList(
    viewItems: List<TopPlatformViewItem>,
    sortingField: SortingField,
    timeDuration: TimeDuration,
    onItemClick: (Platform) -> Unit,
    preItems: LazyListScope.() -> Unit
) {
    val state = rememberSaveable(sortingField, timeDuration, saver = LazyListState.Saver) {
        LazyListState(0, 0)
    }

    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        preItems.invoke(this)
        items(viewItems) { item ->
            TopPlatformItem(item, onItemClick)
        }
    }
}

@Composable
private fun TopPlatformSecondRow(
    subtitle: String,
    marketDataValue: MarketDataValue?,
    rank: String,
    rankDiff: BigDecimal?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgeWithDiff(
            modifier = Modifier.padding(end = 8.dp),
            text = rank,
            diff = rankDiff
        )
        subhead2_grey(
            text = subtitle,
            maxLines = 1,
        )
        marketDataValue?.let {
            Spacer(modifier = Modifier.weight(1f))
            MarketDataValueComponent(marketDataValue)
        }
    }
}

@Composable
fun TopPlatformItem(item: TopPlatformViewItem, onItemClick: (Platform) -> Unit) {
    SectionItemBorderedRowUniversalClear(
        borderBottom = true,
        onClick = { onItemClick(item.platform) }
    ) {
        CoinImage(
            iconUrl = item.iconUrl,
            placeholder = item.iconPlaceHolder,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            MarketCoinFirstRow(item.platform.name, item.marketCap)
            Spacer(modifier = Modifier.height(3.dp))
            TopPlatformSecondRow(
                subtitle = item.subtitle,
                marketDataValue = MarketDataValue.Diff(item.marketCapDiff),
                rank = item.rank.toString(),
                rankDiff = item.rankDiff?.toBigDecimal()
            )
        }
    }
}
