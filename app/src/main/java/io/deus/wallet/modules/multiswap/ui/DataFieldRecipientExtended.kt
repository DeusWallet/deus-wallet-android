package io.deus.wallet.modules.multiswap.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.entities.Address
import io.deus.wallet.modules.multiswap.QuoteInfoRow
import io.deus.wallet.ui.compose.ComposeAppTheme
import io.deus.wallet.ui.compose.components.ButtonSecondaryCircle
import io.deus.wallet.ui.compose.components.HSpacer
import io.deus.wallet.ui.compose.components.subhead2_grey
import io.deus.wallet.ui.compose.components.subhead2_leah
import io.deus.wallet.ui.helpers.TextHelper
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.marketkit.models.BlockchainType

data class DataFieldRecipientExtended(
    val address: Address,
    val blockchainType: BlockchainType
) : DataField {
    @Composable
    override fun GetContent(navController: NavController, borderTop: Boolean) {
        QuoteInfoRow(
            borderTop = borderTop,
            title = {
                subhead2_grey(text = stringResource(R.string.Swap_Recipient))
            },
            value = {
                subhead2_leah(
                    modifier = Modifier.weight(1f, false),
                    text = address.hex,
                    textAlign = TextAlign.End
                )

                val view = LocalView.current
                HSpacer(16.dp)
                ButtonSecondaryCircle(
                    icon = R.drawable.ic_copy_20,
                    onClick = {
                        TextHelper.copyText(address.hex)
                        HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                    }
                )
            }
        )

        val contact = App.contactsRepository.getContactsFiltered(
            blockchainType,
            addressQuery = address.hex
        ).firstOrNull()

        contact?.name?.let { name ->
            QuoteInfoRow(
                borderTop = borderTop,
                title = {
                    subhead2_grey(text = stringResource(R.string.TransactionInfo_ContactName))
                },
                value = {
                    subhead2_leah(
                        text = name,
                        textAlign = TextAlign.End
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun DataFieldRecipientExtendedPreview() {
    val navController = rememberNavController()
    ComposeAppTheme {
        DataFieldRecipientExtended(
            Address("0x1234567890abcdef1234567890abcdef12345678"),
            BlockchainType.Bitcoin
        ).GetContent(navController = navController, borderTop = true)
    }
}
