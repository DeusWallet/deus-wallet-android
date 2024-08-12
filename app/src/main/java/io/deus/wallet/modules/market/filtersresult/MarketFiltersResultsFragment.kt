package io.deus.wallet.modules.market.filtersresult

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.CoinFragment
import io.deus.wallet.modules.coin.overview.ui.Loading
import io.deus.wallet.modules.market.filters.MarketFiltersViewModel
import io.deus.wallet.modules.market.topcoins.SelectorDialogState
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AlertGroup
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.ButtonSecondaryToggle
import io.deus.wallet.ui.compose.components.CoinList
import io.deus.wallet.ui.compose.components.HeaderSorting
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.ListErrorView
import io.deus.wallet.ui.compose.components.SortMenu

class MarketFiltersResultsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = getViewModel()

        if (viewModel == null) {
            navController.popBackStack()
            return
        }

        SearchResultsScreen(viewModel, navController)
    }

    private fun getViewModel(): MarketFiltersResultViewModel? {
        return try {
            val marketSearchFilterViewModel by navGraphViewModels<MarketFiltersViewModel>(R.id.marketAdvancedSearchFragment)
            val viewModel by viewModels<MarketFiltersResultViewModel> {
                MarketFiltersResultsModule.Factory(marketSearchFilterViewModel.service)
            }
            viewModel
        } catch (e: RuntimeException) {
            null
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchResultsScreen(
    viewModel: MarketFiltersResultViewModel,
    navController: NavController
) {

    var scrollToTopAfterUpdate by rememberSaveable { mutableStateOf(false) }

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                title = stringResource(R.string.Market_AdvancedSearch_Results),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )

            Crossfade(viewModel.viewState) { state ->
                when (state) {
                    ViewState.Loading -> {
                        Loading()
                    }

                    is ViewState.Error -> {
                        ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                    }

                    ViewState.Success -> {
                        CoinList(
                            items = viewModel.viewItemsState,
                            scrollToTop = scrollToTopAfterUpdate,
                            onAddFavorite = { uid ->
                                viewModel.onAddFavorite(uid)

                                stat(page = StatPage.AdvancedSearchResults, event = StatEvent.AddToWatchlist(uid))
                            },
                            onRemoveFavorite = { uid ->
                                viewModel.onRemoveFavorite(uid)

                                stat(page = StatPage.AdvancedSearchResults, event = StatEvent.RemoveFromWatchlist(uid))
                            },
                            onCoinClick = { coinUid ->
                                val arguments = CoinFragment.Input(coinUid)
                                navController.slideFromRight(R.id.coinFragment, arguments)

                                stat(page = StatPage.AdvancedSearchResults, event = StatEvent.OpenCoin(coinUid))
                            },
                            preItems = {
                                stickyHeader {
                                    ListHeaderMenu(viewModel)
                                }
                            }
                        )
                        if (scrollToTopAfterUpdate) {
                            scrollToTopAfterUpdate = false
                        }
                    }
                }
            }

        }

        //Dialog
        (viewModel.selectorDialogState as? SelectorDialogState.Opened)?.let { state ->
            AlertGroup(
                title = R.string.Market_Sort_PopupTitle,
                select = state.select,
                onSelect = { selected ->
                    scrollToTopAfterUpdate = true
                    viewModel.onSelectSortingField(selected)
                },
                onDismiss = { viewModel.onSelectorDialogDismiss() }
            )
        }
    }

}

@Composable
private fun ListHeaderMenu(
    viewModel: MarketFiltersResultViewModel,
) {
    HeaderSorting(borderTop = true, borderBottom = true) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
                .height(44.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SortMenu(
                    titleRes = viewModel.menuState.sortingFieldSelect.selected.titleResId,
                    onClick = viewModel::showSelectorMenu
                )
            }

            Box(modifier = Modifier.padding(start = 8.dp)) {
                ButtonSecondaryToggle(
                    select = viewModel.menuState.marketFieldSelect,
                    onSelect = viewModel::marketFieldSelected
                )
            }
        }
    }
}
