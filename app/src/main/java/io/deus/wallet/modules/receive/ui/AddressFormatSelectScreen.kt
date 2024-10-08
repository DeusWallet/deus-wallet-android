package io.deus.wallet.modules.receive.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.deus.wallet.R
import io.deus.wallet.entities.Wallet
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.AppBar
import io.deus.wallet.ui.compose.components.CellUniversalLawrenceSection
import io.deus.wallet.ui.compose.components.HsBackButton
import io.deus.wallet.ui.compose.components.InfoText
import io.deus.wallet.ui.compose.components.RowUniversal
import io.deus.wallet.ui.compose.components.SectionUniversalItem
import io.deus.wallet.ui.compose.components.TextImportantWarning
import io.deus.wallet.ui.compose.components.VSpacer
import io.deus.wallet.ui.compose.components.body_leah
import io.deus.wallet.ui.compose.components.subhead2_grey

@Composable
fun AddressFormatSelectScreen(
    addressFormatItems: List<AddressFormatItem>,
    description: String,
    onSelect: (Wallet) -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.Balance_Receive_AddressFormat),
                navigationIcon = {
                    HsBackButton(onClick = onBackPress)
                },
                menuItems = listOf()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            InfoText(
                text = stringResource(R.string.Balance_Receive_AddressFormatDescription)
            )
            VSpacer(20.dp)
            CellUniversalLawrenceSection(addressFormatItems) { item ->
                SectionUniversalItem {
                    AddressFormatCell(
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            onSelect.invoke(item.wallet)
                        }
                    )
                }
            }
            VSpacer(32.dp)
            TextImportantWarning(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = description
            )
        }
    }
}

@Composable
fun AddressFormatCell(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    RowUniversal(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            body_leah(text = title)
            subhead2_grey(text = subtitle)
        }
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = ComposeAppTheme.colors.grey
        )
    }
}

data class AddressFormatItem(val title: String, val subtitle: String, val wallet: Wallet)