package io.deus.wallet.modules.pin

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.setNavigationResultX
import io.deus.wallet.modules.pin.ui.PinSet
import kotlinx.parcelize.Parcelize

class SetPinFragment : BaseComposeFragment(screenshotEnabled = false) {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()

        PinSet(
            title = stringResource(R.string.PinSet_Title),
            description = stringResource(input?.descriptionResId ?: R.string.PinSet_Info),
            dismissWithSuccess = {
                navController.setNavigationResultX(Result(true))
                navController.popBackStack()
            },
            onBackPress = { navController.popBackStack() }
        )
    }

    @Parcelize
    data class Input(val descriptionResId: Int) : Parcelable

    @Parcelize
    data class Result(val success: Boolean) : Parcelable
}
