package io.deus.wallet.modules.receive.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import io.deus.wallet.R
import io.deus.wallet.core.description
import io.deus.wallet.core.imageUrl
import io.deus.wallet.entities.Account
import io.deus.wallet.entities.Wallet
import io.deus.wallet.modules.receive.viewmodels.NetworkSelectViewModel
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.InfoText
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.SectionUniversalItem
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.compose.components.body_leah
import io.deus.wallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.marketkit.models.FullCoin
import kotlinx.coroutines.launch

@Composable
fun NetworkSelectScreen(
    navController: NavController,
    activeAccount: Account,
    fullCoin: FullCoin,
    onSelect: (Wallet) -> Unit
) {
    val viewModel = viewModel<NetworkSelectViewModel>(factory = NetworkSelectViewModel.Factory(activeAccount, fullCoin))
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.Balance_Network),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
                menuItems = listOf()
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoText(
                    text = stringResource(R.string.Balance_NetworkSelectDescription)
                )
                VSpacer(20.dp)
                CellUniversalLawrenceSection(viewModel.eligibleTokens) { token ->
                    val blockchain = token.blockchain
                    SectionUniversalItem {
                        NetworkCell(
                            title = blockchain.name,
                            subtitle = blockchain.description,
                            imageUrl = blockchain.type.imageUrl,
                            onClick = {
                                coroutineScope.launch {
                                    onSelect.invoke(viewModel.getOrCreateWallet(token))
                                }
                            }
                        )
                    }
                }
                VSpacer(32.dp)
            }
        }
    }
}

@Composable
fun NetworkCell(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: (() -> Unit)? = null
) {
    RowUniversal(
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp),
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                error = painterResource(R.drawable.ic_platform_placeholder_32)
            ),
            contentDescription = null,
        )
        Column(modifier = Modifier.weight(1f)) {
            body_leah(text = title)
            subhead2_grey(text = subtitle)
        }
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = ComposeAppTheme.colors.grey
        )
    }
}
