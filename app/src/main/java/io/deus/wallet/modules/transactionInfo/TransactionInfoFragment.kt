package io.deus.wallet.modules.transactionInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.core.stats.StatEntity
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.modules.coin.CoinFragment
import io.deus.wallet.modules.transactions.TransactionsModule
import io.deus.wallet.modules.transactions.TransactionsViewModel
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.DescriptionCell
import io.deus.wallet.ui.compose.components.MenuItem
import io.deus.wallet.ui.compose.components.PriceWithToggleCell
import io.deus.wallet.ui.compose.components.SectionTitleCell
import io.deus.wallet.ui.compose.components.TitleAndValueCell
import io.deus.wallet.ui.compose.components.TransactionAmountCell
import io.deus.wallet.ui.compose.components.TransactionInfoAddressCell
import io.deus.wallet.ui.compose.components.TransactionInfoBtcLockCell
import io.deus.wallet.ui.compose.components.TransactionInfoCancelCell
import io.deus.wallet.ui.compose.components.TransactionInfoContactCell
import io.deus.wallet.ui.compose.components.TransactionInfoDoubleSpendCell
import io.deus.wallet.ui.compose.components.TransactionInfoExplorerCell
import io.deus.wallet.ui.compose.components.TransactionInfoRawTransaction
import io.deus.wallet.ui.compose.components.TransactionInfoSentToSelfCell
import io.deus.wallet.ui.compose.components.TransactionInfoSpeedUpCell
import io.deus.wallet.ui.compose.components.TransactionInfoStatusCell
import io.deus.wallet.ui.compose.components.TransactionInfoTransactionHashCell
import io.deus.wallet.ui.compose.components.TransactionNftAmountCell
import io.deus.wallet.ui.compose.components.WarningMessageCell
import io.deus.wallet.ui.compose.components.caption_grey
import io.deus.wallet.ui.helpers.LinkHelper

class TransactionInfoFragment : BaseComposeFragment() {

    private val viewModelTxs by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }

    @Composable
    override fun GetContent(navController: NavController) {
        val viewItem = viewModelTxs.tmpItemToShow
        if (viewItem == null) {
            navController.popBackStack(R.id.transactionInfoFragment, true)
            return
        }

        val viewModel by navGraphViewModels<TransactionInfoViewModel>(R.id.transactionInfoFragment) {
            TransactionInfoModule.Factory(viewItem)
        }

        TransactionInfoScreen(viewModel, navController)


    }

}

@Composable
fun TransactionInfoScreen(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.TransactionInfo_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = {
                        navController.popBackStack()
                    }
                )
            )
        )
        TransactionInfo(viewModel, navController)
        TransactionInfoFooter(viewModel.appVersion)
    }
}

@Composable
fun TransactionInfo(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)) {
        items(viewModel.viewItems) { section ->
            TransactionInfoSection(section, navController, viewModel::getRawTransaction)
        }
    }
}

@Composable
private fun TransactionInfoFooter(appVersion: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .size(32.dp),
            painter = painterResource(id = R.drawable.ic_company_logo),
            contentDescription = null,
        )
        caption_grey(
            text = stringResource(
                R.string.Settings_InfoTitleWithVersion,
                appVersion
            ).uppercase()
        )
        Divider(
            modifier = Modifier
                .width(100.dp)
                .padding(top = 8.dp, bottom = 4.5.dp),
            thickness = 0.5.dp,
            color = ComposeAppTheme.colors.steel20
        )
        Text(
            text = stringResource(R.string.Settings_InfoSubtitle),
            style = ComposeAppTheme.typography.micro,
            color = ComposeAppTheme.colors.grey,
        )
    }
}

@Composable
fun TransactionInfoSection(
    section: List<TransactionInfoViewItem>,
    navController: NavController,
    getRawTransaction: () -> String?
) {
    //items without background
    if (section.size == 1) {
        when (val item = section[0]) {
            is TransactionInfoViewItem.WarningMessage -> {
                WarningMessageCell(item.message)
                return
            }
            is TransactionInfoViewItem.Description -> {
                DescriptionCell(text = item.text)
                return
            }
            else -> {
                //do nothing
            }
        }
    }

    CellUniversalLawrenceSection(
        buildList {
            for (viewItem in section) {
                when (viewItem) {
                    is TransactionInfoViewItem.Transaction -> {
                        add {
                            SectionTitleCell(title = viewItem.leftValue, value = viewItem.rightValue, iconResId = viewItem.icon)
                        }
                    }

                    is TransactionInfoViewItem.Amount -> {
                        add {
                            TransactionAmountCell(
                                amountType = viewItem.amountType,
                                fiatAmount = viewItem.fiatValue,
                                coinAmount = viewItem.coinValue,
                                coinIconUrl = viewItem.coinIconUrl,
                                badge = viewItem.badge,
                                coinIconPlaceholder = viewItem.coinIconPlaceholder,
                                onClick = viewItem.coinUid?.let {
                                    {
                                        navController.slideFromRight(R.id.coinFragment, CoinFragment.Input(it))

                                        stat(page = StatPage.TransactionInfo, event = StatEvent.OpenCoin(it))
                                    }
                                }
                            )
                        }
                    }

                    is TransactionInfoViewItem.NftAmount -> {
                        add {
                            TransactionNftAmountCell(
                                viewItem.title,
                                viewItem.nftValue,
                                viewItem.nftName,
                                viewItem.iconUrl,
                                viewItem.iconPlaceholder,
                                viewItem.badge,
                            )
                        }
                    }

                    is TransactionInfoViewItem.Value -> {
                        add {
                            TitleAndValueCell(
                                title = viewItem.title,
                                value = viewItem.value,
                            )
                        }
                    }

                    is TransactionInfoViewItem.PriceWithToggle -> {
                        add {
                            PriceWithToggleCell(
                                title = viewItem.title,
                                valueOne = viewItem.valueTwo,
                                valueTwo = viewItem.valueOne
                            )
                        }
                    }

                    is TransactionInfoViewItem.Address -> {
                        add {
                            TransactionInfoAddressCell(
                                title = viewItem.title,
                                value = viewItem.value,
                                showAdd = viewItem.showAdd,
                                blockchainType = viewItem.blockchainType,
                                navController = navController,
                                onCopy = {
                                    stat(page = StatPage.TransactionInfo, section = viewItem.statSection, event = StatEvent.Copy(StatEntity.Address))
                                },
                                onAddToExisting = {
                                    stat(page = StatPage.TransactionInfo, section = viewItem.statSection, event = StatEvent.Open(StatPage.ContactAddToExisting))
                                },
                                onAddToNew = {
                                    stat(page = StatPage.TransactionInfo, section = viewItem.statSection, event = StatEvent.Open(StatPage.ContactNew))
                                }
                            )
                        }
                    }

                    is TransactionInfoViewItem.ContactItem -> {
                        add {
                            TransactionInfoContactCell(viewItem.contact.name)
                        }
                    }

                    is TransactionInfoViewItem.Status -> {
                        add {
                            TransactionInfoStatusCell(status = viewItem.status, navController = navController)
                        }
                    }

                    is TransactionInfoViewItem.SpeedUpCancel -> {
                        add {
                            TransactionInfoSpeedUpCell(
                                transactionHash = viewItem.transactionHash,
                                blockchainType = viewItem.blockchainType,
                                navController = navController
                            )
                        }
                        add {
                            TransactionInfoCancelCell(
                                transactionHash = viewItem.transactionHash,
                                blockchainType = viewItem.blockchainType,
                                navController = navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.TransactionHash -> {
                        add {
                            TransactionInfoTransactionHashCell(transactionHash = viewItem.transactionHash)
                        }
                    }

                    is TransactionInfoViewItem.Explorer -> {
                        viewItem.url?.let {
                            add {
                                TransactionInfoExplorerCell(title = viewItem.title, url = viewItem.url)
                            }
                        }
                    }

                    is TransactionInfoViewItem.RawTransaction -> {
                        add {
                            TransactionInfoRawTransaction(rawTransaction = getRawTransaction)
                        }
                    }

                    is TransactionInfoViewItem.LockState -> {
                        add {
                            TransactionInfoBtcLockCell(lockState = viewItem, navController = navController)
                        }
                    }

                    is TransactionInfoViewItem.DoubleSpend -> {
                        add {
                            TransactionInfoDoubleSpendCell(
                                transactionHash = viewItem.transactionHash,
                                conflictingHash = viewItem.conflictingHash,
                                navController = navController
                            )
                        }
                    }

                    is TransactionInfoViewItem.SentToSelf -> {
                        add {
                            TransactionInfoSentToSelfCell()
                        }
                    }

                    else -> {
                        //do nothing
                    }
                }
            }
        }
    )
}

