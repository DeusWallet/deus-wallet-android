package io.deus.wallet.modules.settings.faq

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.core.LocalizedException
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.entities.Faq
import io.deus.wallet.entities.ViewState
import io.deus.wallet.modules.coin.overview.ui.Loading
import io.deus.wallet.modules.markdown.MarkdownFragment
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.ScreenMessageWithAction
import io.deus.wallet.ui.compose.components.ScrollableTabs
import io.deus.wallet.ui.compose.components.TabItem
import io.deus.wallet.ui.compose.components.subhead1_leah
import java.net.UnknownHostException

class FaqListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        FaqScreen(
            onCloseClick = { navController.popBackStack() },
            onItemClick = { faqItem ->
                navController.slideFromRight(
                    R.id.markdownFragment,
                    MarkdownFragment.Input(faqItem.markdown)
                )
            }
        )
    }

}

@Composable
private fun FaqScreen(
    onCloseClick: () -> Unit,
    onItemClick: (Faq) -> Unit,
    viewModel: FaqViewModel = viewModel(factory = FaqModule.Factory())
) {
    val viewState = viewModel.viewState
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.Settings_Faq),
            navigationIcon = {
                HsBackButton(onClick = onCloseClick)
            }
        )
        Crossfade(viewState) { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }

                is ViewState.Error -> {
                    val s = when (val error = viewState.t) {
                        is UnknownHostException -> stringResource(R.string.Hud_Text_NoInternet)
                        is LocalizedException -> stringResource(error.errorTextRes)
                        else -> stringResource(R.string.Hud_UnknownError, error)
                    }

                    ScreenMessageWithAction(s, R.drawable.ic_error_48)
                }

                ViewState.Success -> {
                    Column {
                        val tabItems =
                            viewModel.sections.map {
                                TabItem(
                                    it.section,
                                    it == viewModel.selectedSection,
                                    it
                                )
                            }
                        ScrollableTabs(tabItems) { tab ->
                            viewModel.onSelectSection(tab)
                        }

                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Spacer(Modifier.height(12.dp))
                            CellUniversalLawrenceSection(viewModel.faqItems) { faq ->
                                RowUniversal(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { onItemClick(faq) }
                                ) {
                                    subhead1_leah(text = faq.title)
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}
