package io.deus.wallet.modules.walletconnect.list

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.modules.walletconnect.list.ui.WCSessionsScreen
import kotlinx.parcelize.Parcelize

class WCListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()
        WCSessionsScreen(
            navController,
            input?.deepLinkUri
        )
    }

    @Parcelize
    data class Input(val deepLinkUri: String) : Parcelable
}
