package io.deus.wallet.modules.swap.settings.uniswap

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.setNavigationResultX
import io.deus.wallet.entities.Address
import io.deus.wallet.modules.evmfee.ButtonsGroupWithShade
import io.deus.wallet.modules.swap.SwapMainModule
import io.deus.wallet.modules.swap.settings.RecipientAddressViewModel
import io.deus.wallet.modules.swap.settings.SwapDeadlineViewModel
import io.deus.wallet.modules.swap.settings.SwapSlippageViewModel
import io.deus.wallet.modules.swap.settings.ui.RecipientAddress
import io.deus.wallet.modules.swap.settings.ui.SlippageAmount
import io.deus.wallet.modules.swap.settings.ui.TransactionDeadlineInput
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.ButtonPrimaryPurple
import io.deus.wallet.ui.compose.components.MenuItem
import io.deus.wallet.ui.compose.components.ScreenMessageWithAction
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

class UniswapSettingsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()
        if (input != null) {
            UniswapSettingsScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                dex = input.dex,
                factory = UniswapSettingsModule.Factory(input.address, input.slippage, input.ttl),
                navController = navController,
                ttlEnabled = input.ttlEnabled
            )
        } else {
            ScreenMessageWithAction(
                text = stringResource(R.string.Error),
                icon = R.drawable.ic_error_48
            ) {
                ButtonPrimaryPurple(
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.Button_Close),
                    onClick = { navController.popBackStack() }
                )
            }
        }
    }

    @Parcelize
    data class Input(
        val dex: SwapMainModule.Dex,
        val address: Address?,
        val slippage: BigDecimal,
        val ttlEnabled: Boolean,
        val ttl: Long? = null
    ) : Parcelable
}

@Composable
private fun UniswapSettingsScreen(
    onCloseClick: () -> Unit,
    factory: UniswapSettingsModule.Factory,
    dex: SwapMainModule.Dex,
    uniswapSettingsViewModel: UniswapSettingsViewModel = viewModel(factory = factory),
    deadlineViewModel: SwapDeadlineViewModel = viewModel(factory = factory),
    recipientAddressViewModel: RecipientAddressViewModel = viewModel(factory = factory),
    slippageViewModel: SwapSlippageViewModel = viewModel(factory = factory),
    navController: NavController,
    ttlEnabled: Boolean,
) {
    val (buttonTitle, buttonEnabled) = uniswapSettingsViewModel.buttonState
    val view = LocalView.current

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                title = stringResource(R.string.SwapSettings_Title),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = onCloseClick
                    )
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    RecipientAddress(dex.blockchainType, recipientAddressViewModel, navController)

                    Spacer(modifier = Modifier.height(24.dp))
                    SlippageAmount(slippageViewModel)

                    if (ttlEnabled) {
                        Spacer(modifier = Modifier.height(24.dp))
                        TransactionDeadlineInput(deadlineViewModel)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    TextImportantWarning(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.SwapSettings_FeeSettingsAlert)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            ButtonsGroupWithShade {
                ButtonPrimaryPurple(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = buttonTitle,
                    onClick = {
                        val tradeOptions = uniswapSettingsViewModel.tradeOptions
                        if (tradeOptions != null) {
                            navController.setNavigationResultX(
                                SwapMainModule.Result(
                                    tradeOptions.recipient,
                                    tradeOptions.allowedSlippage.toPlainString(),
                                    tradeOptions.ttl
                                )
                            )
                            onCloseClick()
                        } else {
                            HudHelper.showErrorMessage(view, R.string.default_error_msg)
                        }
                    },
                    enabled = buttonEnabled
                )
            }
        }
    }
}
