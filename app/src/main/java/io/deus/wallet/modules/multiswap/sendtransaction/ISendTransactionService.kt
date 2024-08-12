package io.deus.wallet.modules.multiswap.sendtransaction

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.deus.wallet.core.ServiceState
import io.deus.wallet.core.ethereum.CautionViewItem
import io.deus.wallet.modules.multiswap.ui.DataField
import io.deus.wallet.modules.send.SendModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

abstract class ISendTransactionService: ServiceState<SendTransactionServiceState>() {
    abstract fun start(coroutineScope: CoroutineScope)
    abstract fun setSendTransactionData(data: SendTransactionData)
    @Composable
    abstract fun GetSettingsContent(navController: NavController)
    abstract suspend fun sendTransaction()
    abstract val sendTransactionSettingsFlow: StateFlow<SendTransactionSettings>
}

data class SendTransactionServiceState(
    val networkFee: SendModule.AmountData?,
    val cautions: List<CautionViewItem>,
    val sendable: Boolean,
    val loading: Boolean,
    val fields: List<DataField>
)
