package io.deus.wallet.modules.markdown

import android.os.Parcelable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.getInput
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.TranslatableString
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.MenuItem
import kotlinx.parcelize.Parcelize

class MarkdownFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()

        MarkdownScreen(
            handleRelativeUrl = input?.handleRelativeUrl ?: false,
            showAsPopup = input?.showAsPopup ?: false,
            markdownUrl = input?.markdownUrl ?: "",
            onCloseClick = { navController.popBackStack() },
            onUrlClick = { url ->
                navController.slideFromRight(
                    R.id.markdownFragment, MarkdownFragment.Input(url)
                )
            }
        )
    }

    @Parcelize
    data class Input(
        val markdownUrl: String,
        val handleRelativeUrl: Boolean = false,
        val showAsPopup: Boolean = false,
    ) : Parcelable
}

@Composable
private fun MarkdownScreen(
    handleRelativeUrl: Boolean,
    showAsPopup: Boolean,
    markdownUrl: String,
    onCloseClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    viewModel: MarkdownViewModel = viewModel(factory = MarkdownModule.Factory(markdownUrl))
) {

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            if (showAsPopup) {
                AppBar(
                    menuItems = listOf(
                        MenuItem(
                            title = TranslatableString.ResString(R.string.Button_Close),
                            icon = R.drawable.ic_close,
                            onClick = onCloseClick
                        )
                    )
                )
            } else {
                AppBar(navigationIcon = { HsBackButton(onClick = onCloseClick) })
            }
        }
    ) {
        MarkdownContent(
            modifier = Modifier.padding(it),
            viewState = viewModel.viewState,
            markdownBlocks = viewModel.markdownBlocks,
            handleRelativeUrl = handleRelativeUrl,
            onRetryClick = { viewModel.retry() },
            onUrlClick = onUrlClick
        )
    }
}
