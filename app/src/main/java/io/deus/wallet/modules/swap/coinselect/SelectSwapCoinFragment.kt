package io.deus.wallet.modules.swap.coinselect

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.badge
import io.deus.wallet.core.getInput
import io.deus.wallet.core.iconPlaceholder
import io.deus.wallet.core.imageUrl
import io.deus.wallet.core.setNavigationResultX
import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.SwapMainModule.CoinBalanceItem
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.B2
import io.deus.wallet.ui.compose.components.Badge
import io.deus.wallet.ui.compose.components.CoinImage
import io.deus.wallet.ui.compose.components.D1
import io.deus.wallet.ui.compose.components.MultitextM1
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.SearchBar
import io.deus.wallet.ui.compose.components.SectionUniversalItem
import io.deus.wallet.ui.compose.components.VSpacer

class SelectSwapCoinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val dex = navController.getInput<SwapMainModule.Dex>()
        if (dex == null) {
            navController.popBackStack()
        } else {
            val viewModel = viewModel<SelectSwapCoinViewModel>(
                factory = SelectSwapCoinModule.Factory(
                    dex
                )
            )
            SelectSwapCoinDialogScreen(
                title = stringResource(id =R.string.Select_Coins),
                coinBalanceItems = viewModel.coinItems,
                onSearchTextChanged = viewModel::onEnterQuery,
                onClose = navController::popBackStack
            ) {
                navController.setNavigationResultX(it)
                Handler(Looper.getMainLooper()).postDelayed({
                    navController.popBackStack()
                }, 100)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectSwapCoinDialogScreen(
    title: String,
    coinBalanceItems: List<CoinBalanceItem>,
    onSearchTextChanged: (String) -> Unit,
    onClose: () -> Unit,
    onClickItem: (CoinBalanceItem) -> Unit
) {
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        SearchBar(
            title = title,
            searchHintText = stringResource(R.string.ManageCoins_Search),
            onClose = onClose,
            onSearchTextChanged = onSearchTextChanged
        )

        LazyColumn {
            items(coinBalanceItems) { coinItem ->
                SectionUniversalItem(borderTop = true) {
                    RowUniversal(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = {
                            onClickItem.invoke(coinItem)
                        }
                    ) {
                        CoinImage(
                            iconUrl = coinItem.token.coin.imageUrl,
                            placeholder = coinItem.token.iconPlaceholder,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        MultitextM1(
                            title = {
                                Row {
                                    B2(text = coinItem.token.coin.code)
                                    coinItem.token.badge?.let {
                                        Badge(text = it)
                                    }
                                }
                            },
                            subtitle = { D1(text = coinItem.token.coin.name) }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        MultitextM1(
                            title = {
                                coinItem.balance?.let {
                                    App.numberFormatter.formatCoinShort(
                                        it,
                                        coinItem.token.coin.code,
                                        8
                                    )
                                }?.let {
                                    B2(text = it)
                                }
                            },
                            subtitle = {
                                coinItem.fiatBalanceValue?.let { fiatBalanceValue ->
                                    App.numberFormatter.formatFiatShort(
                                        fiatBalanceValue.value,
                                        fiatBalanceValue.currency.symbol,
                                        2
                                    )
                                }?.let {
                                    D1(
                                        modifier = Modifier.align(Alignment.End),
                                        text = it
                                    )
                                }
                            }
                        )
                    }
                }
            }
            item {
                VSpacer(height = 32.dp)
            }
        }
    }
}
