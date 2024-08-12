package io.deus.wallet.modules.multiswap.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.deus.wallet.R
import io.deus.wallet.core.App
import io.deus.wallet.modules.multiswap.QuoteInfoRow
import io.deus.wallet.ui.compose.components.subhead2_grey
import io.deus.wallet.ui.compose.components.subhead2_leah
import java.math.BigDecimal

data class DataFieldSlippage(val slippage: BigDecimal) : DataField {
    @Composable
    override fun GetContent(navController: NavController, borderTop: Boolean) {
        QuoteInfoRow(
            borderTop = borderTop,
            title = {
                subhead2_grey(text = stringResource(R.string.Swap_Slippage))
            },
            value = {
                subhead2_leah(text = App.numberFormatter.format(slippage, 0, 2, suffix = "%"))
            }
        )
    }
}
