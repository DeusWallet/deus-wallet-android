package io.deus.wallet.modules.balance.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.entities.AccountType
import io.deus.wallet.modules.balance.BalanceAccountsViewModel
import io.deus.wallet.modules.balance.BalanceModule
import io.deus.wallet.modules.balance.BalanceScreenState
import io.deus.wallet.modules.balance.cex.BalanceForAccountCex

@Composable
fun BalanceScreen(navController: NavController) {
    val viewModel = viewModel<BalanceAccountsViewModel>(factory = BalanceModule.AccountsFactory())

    when (val tmpAccount = viewModel.balanceScreenState) {
        BalanceScreenState.NoAccount -> BalanceNoAccount(navController)
        is BalanceScreenState.HasAccount -> when (tmpAccount.accountViewItem.type) {
            is AccountType.Cex -> {
                BalanceForAccountCex(navController, tmpAccount.accountViewItem)
            }

            else -> {
                BalanceForAccount(navController, tmpAccount.accountViewItem)
            }
        }

        else -> {}
    }
}