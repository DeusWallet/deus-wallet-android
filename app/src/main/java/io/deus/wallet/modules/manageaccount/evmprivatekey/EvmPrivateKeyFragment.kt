package io.deus.wallet.modules.manageaccount.evmprivatekey

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.managers.FaqManager
import io.deus.wallet.core.stats.StatEntity
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.modules.manageaccount.ui.ActionButton
import io.deus.wallet.modules.manageaccount.ui.ConfirmCopyBottomSheet
import io.deus.wallet.modules.manageaccount.ui.HidableContent
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.MenuItem
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.deus.wallet.ui.helpers.TextHelper
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class EvmPrivateKeyFragment : BaseComposeFragment(screenshotEnabled = false) {

    @Parcelize
    data class Input(val evmPrivateKey: String) : Parcelable

    @Composable
    override fun GetContent(navController: NavController) {
        EvmPrivateKeyScreen(
            navController = navController,
            evmPrivateKey = navController.getInput<Input>()?.evmPrivateKey ?: ""
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EvmPrivateKeyScreen(
    navController: NavController,
    evmPrivateKey: String,
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            ConfirmCopyBottomSheet(
                onConfirm = {
                    coroutineScope.launch {
                        TextHelper.copyText(evmPrivateKey)
                        HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                        sheetState.hide()

                        stat(page = StatPage.EvmPrivateKey, event = StatEvent.Copy(StatEntity.EvmPrivateKey))
                    }
                },
                onCancel = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
            AppBar(
                title = stringResource(R.string.EvmPrivateKey_Title),
                navigationIcon = {
                    HsBackButton(onClick = navController::popBackStack)
                },
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Info_Title),
                        icon = R.drawable.ic_info_24,
                        onClick = {
                            FaqManager.showFaqPage(navController, FaqManager.faqPathPrivateKeys)

                            stat(page = StatPage.EvmPrivateKey, event = StatEvent.Open(StatPage.Info))
                        }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(12.dp))
                TextImportantWarning(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.PrivateKeys_NeverShareWarning)
                )
                Spacer(Modifier.height(24.dp))
                HidableContent(evmPrivateKey, stringResource(R.string.EvmPrivateKey_ShowPrivateKey)) {
                    stat(page = StatPage.EvmPrivateKey, event = StatEvent.ToggleHidden)
                }
            }
            ActionButton(R.string.Alert_Copy) {
                coroutineScope.launch {
                    sheetState.show()
                }
            }
        }
    }
}
