package io.deus.wallet.modules.settings.security

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.authorizedAction
import io.deus.wallet.core.ensurePinSet
import io.deus.wallet.core.slideFromRight
import io.deus.wallet.modules.settings.security.passcode.SecuritySettingsViewModel
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.body_jacob
import io.deus.wallet.ui.compose.components.body_lucian

@Composable
fun DuressPasscodeBlock(
    viewModel: SecuritySettingsViewModel,
    navController: NavController
) {
    val uiState = viewModel.uiState

    CellUniversalLawrenceSection(buildList<@Composable () -> Unit> {
        add {
            SecurityCenterCell(
                start = {
                    Icon(
                        painter = painterResource(R.drawable.ic_switch_wallet_24),
                        tint = ComposeAppTheme.colors.jacob,
                        modifier = Modifier.size(24.dp),
                        contentDescription = null,
                    )
                },
                center = {
                    val text = if (uiState.duressPinEnabled) {
                        R.string.SettingsSecurity_EditDuressPin
                    } else {
                        R.string.SettingsSecurity_SetDuressPin
                    }
                    body_jacob(
                        text = stringResource(text),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                onClick = {
                    if (uiState.pinEnabled) {
                        navController.authorizedAction {
                            if (uiState.duressPinEnabled) {
                                navController.slideFromRight(R.id.editDuressPinFragment)
                            } else {
                                navController.slideFromRight(R.id.setDuressPinIntroFragment)
                            }
                        }
                    } else {
                        navController.ensurePinSet(R.string.PinSet_ForDuress) {
                            navController.slideFromRight(R.id.setDuressPinIntroFragment)
                        }
                    }
                }
            )
        }
        if (uiState.duressPinEnabled) {
            add {
                SecurityCenterCell(
                    start = {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete_20),
                            tint = ComposeAppTheme.colors.lucian,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null,
                        )
                    },
                    center = {
                        body_lucian(
                            text = stringResource(R.string.SettingsSecurity_DisableDuressPin),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        navController.authorizedAction {
                            viewModel.disableDuressPin()
                        }
                    }
                )
            }
        }
    })
}
