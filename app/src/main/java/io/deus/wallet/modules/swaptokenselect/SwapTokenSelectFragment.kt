package io.deus.wallet.modules.swaptokenselect

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.modules.swap.SwapMainFragment
import io.deus.wallet.modules.tokenselect.TokenSelectScreen
import io.deus.wallet.modules.tokenselect.TokenSelectViewModel
import io.horizontalsystems.core.helpers.HudHelper

class SwapTokenSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val view = LocalView.current
        TokenSelectScreen(
            navController = navController,
            title = stringResource(R.string.Balance_Swap),
            onClickItem = {
                when {
                    it.swapEnabled -> {
                        navController.slideFromRight(
                            R.id.swapFragment,
                            SwapMainFragment.Input(it.wallet.token, R.id.swapTokenSelectFragment)
                        )
                    }
                    it.syncingProgress.progress != null -> {
                        HudHelper.showWarningMessage(view, R.string.Hud_WaitForSynchronization)
                    }
                    it.errorMessage != null -> {
                        HudHelper.showErrorMessage(view, it.errorMessage ?: "")
                    }
                }
            },
            viewModel = viewModel(factory = TokenSelectViewModel.FactoryForSwap()),
            emptyItemsText = stringResource(R.string.Balance_NoAssetsToSwap)
        )
    }

}