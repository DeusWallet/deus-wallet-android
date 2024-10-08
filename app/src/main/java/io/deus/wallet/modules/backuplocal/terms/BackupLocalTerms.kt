package io.deus.wallet.modules.backuplocal.terms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.deus.wallet.R
import io.deus.wallet.modules.evmfee.ButtonsGroupWithShade
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.ButtonPrimaryPurple
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.HsCheckbox
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.compose.components.subhead2_leah

@Composable
fun LocalBackupTermsScreen(
    onTermsAccepted: () -> Unit,
    onBackClick: () -> Unit,
) {
    var termChecked by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.LocalBackup_Title),
                navigationIcon = {
                    HsBackButton(onClick = onBackClick)
                },
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.weight(1f)) {
                TextImportantWarning(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = stringResource(R.string.LocalBackup_TermsWarningText)
                )
                VSpacer(24.dp)
                CellUniversalLawrenceSection(
                    listOf {
                        LocalBackupTerm(
                            text = stringResource(R.string.LocalBackup_Term1),
                            checked = termChecked,
                            onCheckedChange = { checked ->
                                termChecked = checked
                            }
                        )
                    }
                )
            }
            ButtonsGroupWithShade {
                ButtonPrimaryPurple(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    title = stringResource(R.string.Button_Continue),
                    enabled = termChecked,
                    onClick = onTermsAccepted,
                )
            }
        }
    }
}

@Composable
private fun LocalBackupTerm(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = {
            onCheckedChange.invoke(checked.not())
        }
    ) {
        HsCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(Modifier.width(16.dp))
        subhead2_leah(text = text)
    }
}
