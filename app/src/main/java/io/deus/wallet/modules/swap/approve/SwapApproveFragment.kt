package io.deus.wallet.modules.swap.approve

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.requireInput
import io.deus.wallet.core.setNavigationResultX
import io.deus.wallet.core.slideFromRightForResult
import io.deus.wallet.entities.DataState
import io.deus.wallet.modules.evmfee.ButtonsGroupWithShade
import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.approve.confirmation.SwapApproveConfirmationFragment
import io.deus.wallet.modules.swap.approve.confirmation.SwapApproveConfirmationModule
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.ButtonPrimaryPurple
import io.deus.wallet.ui.compose.components.FormsInput
import io.deus.wallet.ui.compose.components.MenuItem
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.deus.wallet.ui.compose.components.TextPreprocessor

class SwapApproveFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val approveData = navController.requireInput<SwapMainModule.ApproveData>()
        SwapApproveScreen(navController, approveData)
    }

}

@Composable
fun SwapApproveScreen(
    navController: NavController,
    approveData: SwapMainModule.ApproveData
) {
    val swapApproveViewModel =
        viewModel<SwapApproveViewModel>(factory = SwapApproveModule.Factory(approveData))

    val approveAllowed = swapApproveViewModel.approveAllowed
    val amountError = swapApproveViewModel.amountError

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.Approve_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Button_Close),
                    icon = R.drawable.ic_close,
                    onClick = navController::popBackStack
                )
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
        TextImportantWarning(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.Approve_Info)
        )

        Spacer(modifier = Modifier.height(12.dp))
        val state = amountError?.let {
            DataState.Error(it)
        }
        var validAmount by rememberSaveable { mutableStateOf(swapApproveViewModel.initialAmount) }
        FormsInput(
            modifier = Modifier.padding(horizontal = 16.dp),
            initial = swapApproveViewModel.initialAmount,
            hint = "",
            state = state,
            pasteEnabled = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textPreprocessor = object : TextPreprocessor {
                override fun process(text: String): String {
                    if (swapApproveViewModel.validateAmount(text)) {
                        validAmount = text
                    } else {
                        // todo: shake animation
                    }
                    return validAmount
                }
            },
            onValueChange = {
                swapApproveViewModel.onEnterAmount(it)
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        ButtonsGroupWithShade {
            ButtonPrimaryPurple(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                title = stringResource(R.string.Swap_Proceed),
                onClick = {
                    swapApproveViewModel.getSendEvmData()?.let { sendEvmData ->
                        navController.slideFromRightForResult<SwapApproveConfirmationFragment.Result>(
                            R.id.swapApproveConfirmationFragment,
                            SwapApproveConfirmationModule.Input(sendEvmData, swapApproveViewModel.blockchainType)
                        ) {
                            navController.setNavigationResultX(it)
                            navController.popBackStack()
                        }
                    }
                },
                enabled = approveAllowed
            )
        }
    }
}
