package io.deus.wallet.modules.pin

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.modules.pin.ui.PinSet
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.parcelize.Parcelize

class SetDuressPinFragment : BaseComposeFragment(screenshotEnabled = false) {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = viewModel<SetDuressPinViewModel>(
            factory = SetDuressPinViewModel.Factory(navController.getInput())
        )
        val view = LocalView.current
        PinSet(
            title = stringResource(id = R.string.SetDuressPin_Title),
            description = stringResource(id = R.string.SetDuressPin_Description),
            dismissWithSuccess = {
                viewModel.onDuressPinSet()
                HudHelper.showSuccessMessage(view, R.string.Hud_Text_Created)
                navController.popBackStack(R.id.setDuressPinIntroFragment, true)
            },
            onBackPress = { navController.popBackStack() },
            forDuress = true
        )
    }

    @Parcelize
    data class Input(val accountIds: List<String>) : Parcelable
}
