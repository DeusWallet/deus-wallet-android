package io.deus.wallet.modules.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.BaseComposeFragment
import io.deus.wallet.modules.main.MainModule
import io.deus.wallet.modules.settings.security.passcode.SecurityPasscodeSettingsModule
import io.deus.wallet.modules.settings.security.passcode.SecuritySettingsViewModel
import io.deus.wallet.modules.settings.security.tor.SecurityTorSettingsModule
import io.deus.wallet.modules.settings.security.tor.SecurityTorSettingsViewModel
import io.deus.wallet.modules.settings.security.ui.PasscodeBlock
import io.deus.wallet.modules.settings.security.ui.TorBlock
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.DisposableLifecycleCallbacks
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.HsSwitch
import io.deus.wallet.ui.compose.components.InfoText
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.compose.components.body_leah
import io.deus.wallet.ui.extensions.ConfirmationDialog
import kotlin.system.exitProcess

class SecuritySettingsFragment : BaseComposeFragment() {

    private val torViewModel by viewModels<SecurityTorSettingsViewModel> {
        SecurityTorSettingsModule.Factory()
    }

    private val securitySettingsViewModel by viewModels<SecuritySettingsViewModel> {
        SecurityPasscodeSettingsModule.Factory()
    }

    @Composable
    override fun GetContent(navController: NavController) {
        SecurityCenterScreen(
            securitySettingsViewModel = securitySettingsViewModel,
            torViewModel = torViewModel,
            navController = navController,
            showAppRestartAlert = { showAppRestartAlert() },
            restartApp = { restartApp() },
        )
    }

    private fun showAppRestartAlert() {
        val warningTitle = if (torViewModel.torCheckEnabled) {
            getString(R.string.Tor_Connection_Enable)
        } else {
            getString(R.string.Tor_Connection_Disable)
        }

        val actionButton = if (torViewModel.torCheckEnabled) {
            getString(R.string.Button_Enable)
        } else {
            getString(R.string.Button_Disable)
        }

        ConfirmationDialog.show(
            icon = R.drawable.ic_tor_connection_24,
            title = getString(R.string.Tor_Alert_Title),
            warningTitle = warningTitle,
            warningText = getString(R.string.SettingsSecurity_AppRestartWarning),
            actionButtonTitle = actionButton,
            transparentButtonTitle = getString(R.string.Alert_Cancel),
            fragmentManager = childFragmentManager,
            listener = object : ConfirmationDialog.Listener {
                override fun onActionButtonClick() {
                    torViewModel.setTorEnabled()
                }

                override fun onTransparentButtonClick() {
                    torViewModel.resetSwitch()
                }

                override fun onCancelButtonClick() {
                    torViewModel.resetSwitch()
                }
            }
        )
    }

    private fun restartApp() {
        activity?.let {
            MainModule.startAsNewTask(it)
            exitProcess(0)
        }
    }
}

@Composable
private fun SecurityCenterScreen(
    securitySettingsViewModel: SecuritySettingsViewModel,
    torViewModel: SecurityTorSettingsViewModel,
    navController: NavController,
    showAppRestartAlert: () -> Unit,
    restartApp: () -> Unit,
) {

    DisposableLifecycleCallbacks(
        onResume = {
            securitySettingsViewModel.update()
        },
    )

    if (torViewModel.restartApp) {
        restartApp()
        torViewModel.appRestarted()
    }

    val uiState = securitySettingsViewModel.uiState
    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.Settings_SecurityCenter),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            PasscodeBlock(
                securitySettingsViewModel,
                navController
            )

            VSpacer(height = 32.dp)

            CellUniversalLawrenceSection {
                SecurityCenterCell(
                    start = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_off_24),
                            tint = ComposeAppTheme.colors.grey,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    },
                    center = {
                        body_leah(
                            text = stringResource(id = R.string.Appearance_BalanceAutoHide),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    end = {
                        HsSwitch(
                            checked = uiState.balanceAutoHideEnabled,
                            onCheckedChange = {
                                securitySettingsViewModel.onSetBalanceAutoHidden(it)
                            }
                        )
                    }
                )
            }
            InfoText(
                text = stringResource(R.string.Appearance_BalanceAutoHide_Description),
                paddingBottom = 32.dp
            )

            TorBlock(
                torViewModel,
                showAppRestartAlert,
            )

            DuressPasscodeBlock(
                securitySettingsViewModel,
                navController
            )
            InfoText(text = stringResource(R.string.SettingsSecurity_DuressPinDescription))

            VSpacer(height = 32.dp)
        }
    }
}

@Composable
fun SecurityCenterCell(
    start: @Composable RowScope.() -> Unit,
    center: @Composable RowScope.() -> Unit,
    end: @Composable() (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        start.invoke(this)
        Spacer(Modifier.width(16.dp))
        center.invoke(this)
        end?.let {
            Spacer(
                Modifier
                    .defaultMinSize(minWidth = 8.dp)
                    .weight(1f)
            )
            end.invoke(this)
        }
    }
}