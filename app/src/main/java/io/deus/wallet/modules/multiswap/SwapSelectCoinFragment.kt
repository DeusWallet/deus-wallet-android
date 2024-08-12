package io.deus.wallet.modules.multiswap

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.setNavigationResultX
import io.deus.wallet.modules.swap.coinselect.SelectSwapCoinDialogScreen
import io.horizontalsystems.marketkit.models.Token
import kotlinx.parcelize.Parcelize

class SwapSelectCoinFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()

        SwapSelectCoinScreen(navController, input?.token, input?.title)
    }

    @Parcelize
    data class Input(val token: Token?, val title: String) : Parcelable

}

@Composable
private fun SwapSelectCoinScreen(
    navController: NavController,
    token: Token?,
    title: String?
) {
    val viewModel = viewModel<SwapSelectCoinViewModel>(
        factory = SwapSelectCoinViewModel.Factory(token)
    )
    val uiState = viewModel.uiState

    SelectSwapCoinDialogScreen(
        title = title ?: "",
        coinBalanceItems = uiState.coinBalanceItems,
        onSearchTextChanged = viewModel::setQuery,
        onClose = navController::popBackStack
    ) {
        navController.setNavigationResultX(it.token)
        navController.popBackStack()
    }
}
