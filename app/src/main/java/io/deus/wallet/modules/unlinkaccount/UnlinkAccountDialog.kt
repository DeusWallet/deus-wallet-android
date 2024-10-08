package io.deus.wallet.modules.unlinkaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.requireInput
import io.deus.wallet.core.stats.StatEntity
import io.deus.wallet.core.stats.StatEvent
import io.deus.wallet.core.stats.StatPage
import io.deus.wallet.core.stats.stat
import io.deus.wallet.entities.Account
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.ButtonPrimaryRed
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsCheckbox
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.deus.wallet.ui.compose.components.subhead2_leah
import io.deus.wallet.ui.extensions.BaseComposableBottomSheetFragment
import io.deus.wallet.ui.extensions.BottomSheetHeader
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper

class UnlinkAccountDialog : BaseComposableBottomSheetFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                val navController = findNavController()

                ComposeAppTheme {
                    UnlinkAccountScreen(navController, navController.requireInput())
                }
            }
        }
    }
}

@Composable
private fun UnlinkAccountScreen(navController: NavController, account: Account) {
    val viewModel =
        viewModel<UnlinkAccountViewModel>(factory = UnlinkAccountModule.Factory(account))

    val confirmations = viewModel.confirmations
    val unlinkEnabled = viewModel.unlinkEnabled
    val showDeleteWarning = viewModel.showDeleteWarning

    BottomSheetHeader(
        iconPainter = painterResource(R.drawable.ic_attention_red_24),
        title = stringResource(R.string.ManageKeys_Delete_Title),
        onCloseClick = {
            navController.popBackStack()
        }
    ) {

        Spacer(Modifier.height(12.dp))
        CellUniversalLawrenceSection(confirmations, showFrame = true) { item ->
            RowUniversal(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { viewModel.toggleConfirm(item) }
            ) {
                HsCheckbox(
                    checked = item.confirmed,
                    onCheckedChange = {
                        viewModel.toggleConfirm(item)
                    },
                )
                Spacer(Modifier.width(16.dp))
                subhead2_leah(
                    text = item.confirmationType.title.getString(),
                )
            }
        }

        if (showDeleteWarning) {
            TextImportantWarning(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(id = R.string.ManageAccount_DeleteWarning)
            )
        }

        val view = LocalView.current
        val doneConfirmationMessage = stringResource(R.string.Hud_Text_Done)

        Spacer(Modifier.height(32.dp))
        ButtonPrimaryRed(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            title = stringResource(viewModel.deleteButtonText),
            onClick = {
                viewModel.onUnlink()
                HudHelper.showSuccessMessage(view, doneConfirmationMessage)
                navController.popBackStack()

                stat(page = StatPage.UnlinkWallet, event = StatEvent.Delete(StatEntity.Wallet))
            },
            enabled = unlinkEnabled
        )
        Spacer(Modifier.height(32.dp))
    }
}
