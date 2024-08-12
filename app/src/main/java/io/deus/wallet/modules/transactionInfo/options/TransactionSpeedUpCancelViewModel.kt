package io.deus.wallet.modules.transactionInfo.options

import androidx.lifecycle.ViewModel
import io.deus.wallet.R
import io.deus.wallet.core.providers.Translator

class TransactionSpeedUpCancelViewModel(
    optionType: TransactionInfoOptionsModule.Type,
    val isTransactionPending: Boolean
) : ViewModel() {

    val title: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Title)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Title)
    }

    val buttonTitle: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Button)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Button)
    }

}
