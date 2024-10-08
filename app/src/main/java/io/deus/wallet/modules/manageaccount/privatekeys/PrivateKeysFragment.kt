package io.deus.wallet.modules.manageaccount.privatekeys

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.authorizedAction
import io.deus.wallet.core.getInput
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.entities.Account
import io.deus.wallet.modules.manageaccount.evmprivatekey.EvmPrivateKeyFragment
import io.deus.wallet.modules.manageaccount.showextendedkey.ShowExtendedKeyFragment
import io.deus.wallet.modules.manageaccount.ui.KeyActionItem
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.HsBackButton

class PrivateKeysFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val account = navController.getInput<Account>()
        if (account == null) {
            Toast.makeText(App.instance, "Account parameter is missing", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            return
        }
        ManageAccountScreen(navController, account)
    }

}

@Composable
fun ManageAccountScreen(navController: NavController, account: Account) {
    val viewModel = viewModel<PrivateKeysViewModel>(factory = PrivateKeysModule.Factory(account))

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.PrivateKeys_Title),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            viewModel.viewState.evmPrivateKey?.let { key ->
                KeyActionItem(
                    title = stringResource(id = R.string.PrivateKeys_EvmPrivateKey),
                    description = stringResource(R.string.PrivateKeys_EvmPrivateKeyDescription)
                ) {
                    navController.authorizedAction {
                        navController.slideFromRight(
                            R.id.evmPrivateKeyFragment,
                            EvmPrivateKeyFragment.Input(key)
                        )

                        stat(page = StatPage.PrivateKeys, event = StatEvent.Open(StatPage.EvmPrivateKey))
                    }
                }
            }
            viewModel.viewState.bip32RootKey?.let { key ->
                KeyActionItem(
                    title = stringResource(id = R.string.PrivateKeys_Bip32RootKey),
                    description = stringResource(id = R.string.PrivateKeys_Bip32RootKeyDescription),
                ) {
                    navController.authorizedAction {
                        navController.slideFromRight(
                            R.id.showExtendedKeyFragment,
                            ShowExtendedKeyFragment.Input(
                                key.hdKey,
                                key.displayKeyType
                            )
                        )

                        stat(page = StatPage.PrivateKeys, event = StatEvent.Open(StatPage.Bip32RootKey))
                    }
                }
            }
            viewModel.viewState.accountExtendedPrivateKey?.let { key ->
                KeyActionItem(
                    title = stringResource(id = R.string.PrivateKeys_AccountExtendedPrivateKey),
                    description = stringResource(id = R.string.PrivateKeys_AccountExtendedPrivateKeyDescription),
                ) {
                    navController.authorizedAction {
                        navController.slideFromRight(
                            R.id.showExtendedKeyFragment,
                            ShowExtendedKeyFragment.Input(key.hdKey, key.displayKeyType)
                        )

                        stat(page = StatPage.PrivateKeys, event = StatEvent.Open(StatPage.AccountExtendedPrivateKey))
                    }
                }
            }
        }
    }
}
