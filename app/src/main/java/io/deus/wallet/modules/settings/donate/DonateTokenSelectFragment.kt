package io.deus.wallet.modules.settings.donate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.providers.Translator
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.modules.send.SendFragment
import io.deus.wallet.modules.tokenselect.TokenSelectScreen
import io.deus.wallet.modules.tokenselect.TokenSelectViewModel
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.ButtonPrimaryDefault
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.compose.components.headline2_leah
import io.deus.wallet.ui.compose.components.subhead2_grey

class DonateTokenSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        TokenSelectScreen(
            navController = navController,
            title = stringResource(R.string.Settings_DonateWith),
            onClickItem = {
                val donateAddress: String? = App.appConfigProvider.donateAddresses[it.wallet.token.blockchainType]
                val sendTitle = Translator.getString(R.string.Settings_DonateToken, it.wallet.token.fullCoin.coin.code)
                navController.slideFromRight(
                    R.id.sendXFragment,
                    SendFragment.Input(
                        it.wallet,
                        sendTitle,
                        R.id.sendTokenSelectFragment,
                        donateAddress,
                    )
                )

                stat(page = StatPage.Donate, event = StatEvent.OpenSend(it.wallet.token))
            },
            viewModel = viewModel(factory = TokenSelectViewModel.FactoryForSend()),
            emptyItemsText = stringResource(R.string.Balance_NoAssetsToSend)
        ) { DonateHeader(navController) }
    }
}

@Composable
private fun DonateHeader(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VSpacer(24.dp)
        headline2_leah(
            text = stringResource(R.string.Settings_Donate_Info),
            textAlign = TextAlign.Center
        )
        VSpacer(24.dp)
        Icon(
            painter = painterResource(id = R.drawable.ic_heart_filled_24),
            tint = ComposeAppTheme.colors.jacob,
            contentDescription = null,
        )
    }

    GetAddressCell {
        navController.slideFromRight(R.id.donateAddressesFragment)

        stat(page = StatPage.Donate, event = StatEvent.Open(StatPage.DonateAddressList))
    }
}

@Composable
private fun GetAddressCell(
    onClick: () -> Unit
) {
    VSpacer(24.dp)
    ButtonPrimaryDefault(
        title = stringResource(R.string.Settings_Donate_GetAddress),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        onClick = onClick
    )
    VSpacer(24.dp)
    subhead2_grey(
        text = stringResource(R.string.Settings_Donate_OrSelectCoinToDonate),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    VSpacer(24.dp)
}

@Preview
@Composable
private fun DonateHeaderPreview() {
    ComposeAppTheme {
        DonateHeader(navController = rememberNavController())
    }
}